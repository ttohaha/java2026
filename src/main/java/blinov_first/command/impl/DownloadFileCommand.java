package blinov_first.command.impl;

import blinov_first.command.Command;
import blinov_first.entity.MediaFile;
import blinov_first.exception.CommandException;
import blinov_first.exception.ServiceException;
import blinov_first.factory.MediaFileServiceFactory;
import blinov_first.service.MediaFileService;
import blinov_first.util.AttributeName;
import blinov_first.util.PagePath;
import blinov_first.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DownloadFileCommand implements Command {

    private static final Logger LOGGER = LogManager.getLogger(DownloadFileCommand.class);

    @Override
    public String execute(HttpServletRequest request) throws CommandException {
        if (!SessionUtil.isLoggedIn(request)) return PagePath.INDEX;
        Long userId = SessionUtil.getUserId(request);
        if (userId == null) return PagePath.INDEX;

        String idStr = request.getParameter(AttributeName.FILE_ID);
        if (idStr == null) return PagePath.MEDIA_LIST;

        try {
            int fileId = Integer.parseInt(idStr);
            MediaFileService service = MediaFileServiceFactory.getMediaFileService();
            MediaFile file = service.getFileForDownload(fileId, userId);

            if (file == null || !Files.exists(Paths.get(file.getFilePath()))) {
                request.setAttribute(AttributeName.ERROR_MSG, "File not found");
                return PagePath.MEDIA_LIST;
            }

            HttpServletResponse response = (HttpServletResponse) request.getAttribute("__HTTP_RESPONSE__");
            response.setContentType(file.getContentType());
            response.setContentLengthLong(file.getFileSize());
            response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getOriginalFilename() + "\"");

            try (InputStream in = Files.newInputStream(Paths.get(file.getFilePath()));
                 OutputStream out = response.getOutputStream()) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
            return null;
        } catch (NumberFormatException | IOException | ServiceException e) {
            LOGGER.error("File download failed", e);
            throw new CommandException("Download failed", e);
        }
    }
}