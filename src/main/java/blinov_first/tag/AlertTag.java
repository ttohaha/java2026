package blinov_first.tag;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.tagext.SimpleTagSupport;

import java.io.IOException;

public class AlertTag extends SimpleTagSupport {

    private static final String TYPE_SUCCESS = "success";
    private static final String TYPE_DANGER  = "danger";
    private static final String TYPE_WARNING = "warning";

    private String type;
    private String message;

    @Override
    public void doTag() throws JspException, IOException {
        if (message == null || message.isBlank()) {
            return;
        }

        String icon = resolveIcon(type);

        JspWriter out = getJspContext().getOut();
        out.write("<div class=\"alert alert-" + escapeHtml(type) + "\">");
        out.write("<span class=\"alert-icon\">" + icon + "</span>");
        out.write("<span>" + escapeHtml(message) + "</span>");
        out.write("</div>");
    }

    private String resolveIcon(String alertType) {
        if (alertType == null) return "&#8505;";
        return switch (alertType.toLowerCase()) {
            case TYPE_SUCCESS -> "&#10003;";
            case TYPE_DANGER  -> "&#9888;";
            case TYPE_WARNING -> "&#9888;&#65039;";
            default           -> "&#8505;";
        };
    }

    private String escapeHtml(String value) {
        if (value == null) return "";
        return value
                .replace("&",  "&amp;")
                .replace("<",  "&lt;")
                .replace(">",  "&gt;")
                .replace("\"", "&quot;");
    }

    public void setType(String type) {
        this.type = (type != null) ? type.toLowerCase() : TYPE_DANGER;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
