package blinov_first.command.impl;

import blinov_first.command.Command;
import blinov_first.exception.CommandException;
import blinov_first.exception.ServiceException;
import blinov_first.factory.MediaFileServiceFactory;
import blinov_first.util.AttributeName;
import blinov_first.util.FlashMessage;
import blinov_first.util.PagePath;
import blinov_first.util.SessionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class UploadFileCommand implements Command {

    private static final Logger LOGGER = LogManager.getLogger(UploadFileCommand.class);

    @Override
    public String execute(HttpServletRequest request) throws CommandException {
        if (!SessionUtil.isLoggedIn(request)) return PagePath.INDEX;

        Long userId = SessionUtil.getUserId(request);
        if (userId == null) return PagePath.INDEX;

        if ("GET".equalsIgnoreCase(request.getMethod())) {
            return PagePath.UPLOAD_FORM;
        }

        try {
            Part filePart = request.getPart("file");
            if (filePart == null || filePart.getSize() == 0) {
                request.setAttribute(AttributeName.ERROR_MSG, "No file selected or file is empty");
                return PagePath.UPLOAD_FORM;
            }

            String originalName = filePart.getSubmittedFileName();
            String contentType  = filePart.getContentType();
            long   size         = filePart.getSize();

            boolean success = MediaFileServiceFactory.getMediaFileService()
                    .uploadFile(filePart.getInputStream(), originalName, contentType, size, userId);

            if (success) {
                FlashMessage.success(request, "File uploaded successfully");
                return "redirect:/controller?command=list_files";
            } else {
                request.setAttribute(AttributeName.ERROR_MSG, "Upload failed");
                return PagePath.UPLOAD_FORM;
            }
        } catch (IOException | ServletException | ServiceException e) {
            LOGGER.error("File upload error", e);
            request.setAttribute(AttributeName.ERROR_MSG, "Upload error: " + e.getMessage());
            return PagePath.UPLOAD_FORM;
        }
    }
}
