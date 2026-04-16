package blinov_first.command.impl;

import blinov_first.command.Command;
import blinov_first.entity.MediaFile;
import blinov_first.exception.CommandException;
import blinov_first.exception.ServiceException;
import blinov_first.factory.MediaFileServiceFactory;
import blinov_first.service.MediaFileService;
import blinov_first.util.PagePath;
import blinov_first.util.SessionUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class ListFilesCommand implements Command {

    private static final Logger LOGGER = LogManager.getLogger(ListFilesCommand.class);

    @Override
    public String execute(HttpServletRequest request) throws CommandException {
        if (!SessionUtil.isLoggedIn(request)) return PagePath.INDEX;
        Long userId = SessionUtil.getUserId(request);
        if (userId == null) return PagePath.INDEX;

        try {
            MediaFileService service = MediaFileServiceFactory.getMediaFileService();
            List<MediaFile> files = service.getUserFiles(userId);
            request.setAttribute("fileList", files);
            return PagePath.MEDIA_LIST;
        } catch (ServiceException e) {
            LOGGER.error("Failed to list files", e);
            throw new CommandException("File listing failed", e);
        }
    }
}