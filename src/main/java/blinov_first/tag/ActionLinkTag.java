package blinov_first.tag;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.JspWriter;
import jakarta.servlet.jsp.tagext.SimpleTagSupport;

import java.io.IOException;

public class ActionLinkTag extends SimpleTagSupport {

    private String href;
    private String label;
    private String style;
    private String confirm;

    @Override
    public void doTag() throws JspException, IOException {
        if (href == null || label == null) {
            return;
        }

        String cssClass = resolveCssClass(style);

        JspWriter out = getJspContext().getOut();
        out.write("<a href=\"" + escapeHtml(href) + "\" class=\"btn btn-sm " + cssClass + "\"");

        if (confirm != null && !confirm.isBlank()) {
            out.write(" data-confirm=\"" + escapeHtml(confirm) + "\"");
        }

        out.write(">" + escapeHtml(label) + "</a>");
    }

    private String resolveCssClass(String styleAttr) {
        if (styleAttr == null) return "btn-outline";
        return switch (styleAttr.toLowerCase()) {
            case "danger"  -> "btn-danger";
            case "success" -> "btn-success";
            default        -> "btn-outline";
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

    public void setHref(String href)       { this.href = href; }
    public void setLabel(String label)     { this.label = label; }
    public void setStyle(String style)     { this.style = style; }
    public void setConfirm(String confirm) { this.confirm = confirm; }
}
