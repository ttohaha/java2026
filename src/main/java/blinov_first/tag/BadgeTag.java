package blinov_first.tag;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.tagext.SimpleTagSupport;

import java.io.IOException;

public class BadgeTag extends SimpleTagSupport {

    private static final String STATUS_ACTIVE   = "active";
    private static final String STATUS_INACTIVE = "inactive";

    private String status;
    private String labelActive;
    private String labelInactive;

    @Override
    public void doTag() throws JspException, IOException {
        boolean isActive = STATUS_ACTIVE.equalsIgnoreCase(status);

        String cssClass = isActive ? "badge badge-success" : "badge badge-danger";
        String label    = isActive
                ? (labelActive   != null ? labelActive   : "Active")
                : (labelInactive != null ? labelInactive : "Inactive");

        JspWriter out = getJspContext().getOut();
        out.write("<span class=\"" + cssClass + "\">" + escapeHtml(label) + "</span>");
    }

    private String escapeHtml(String value) {
        if (value == null) return "";
        return value
                .replace("&",  "&amp;")
                .replace("<",  "&lt;")
                .replace(">",  "&gt;")
                .replace("\"", "&quot;");
    }

    public void setStatus(String status)           { this.status = status; }
    public void setLabelActive(String labelActive)   { this.labelActive = labelActive; }
    public void setLabelInactive(String labelInactive) { this.labelInactive = labelInactive; }
}
