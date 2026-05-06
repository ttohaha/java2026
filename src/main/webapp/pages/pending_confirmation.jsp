<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c"   uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ include file="fragments/locale_setup.jsp" %>
<!DOCTYPE html>
<html lang="${lang}">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Confirm your account — <fmt:message key="app.title"/></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <style>
        .confirm-methods {
            display: flex;
            flex-direction: column;
            gap: 20px;
            margin-top: 24px;
        }
        .confirm-method {
            border: 1.5px solid var(--color-border);
            border-radius: var(--radius-md);
            padding: 20px 24px;
            background: var(--color-surface);
        }
        .confirm-method h3 {
            margin: 0 0 8px 0;
            font-size: 1rem;
            display: flex;
            align-items: center;
            gap: 8px;
        }
        .confirm-method p {
            margin: 0;
            color: var(--color-text-muted);
            font-size: .9rem;
        }
        .tg-command {
            display: inline-block;
            margin-top: 12px;
            background: var(--color-bg);
            border: 1px solid var(--color-border);
            border-radius: var(--radius-sm);
            padding: 8px 14px;
            font-family: monospace;
            font-size: .95rem;
            letter-spacing: .02em;
            user-select: all;
            cursor: pointer;
        }
        .tg-link {
            display: inline-block;
            margin-top: 12px;
            color: var(--color-primary);
            font-weight: 600;
            text-decoration: none;
        }
        .tg-link:hover { text-decoration: underline; }
    </style>
</head>
<body class="auth-page">

<div class="auth-brand">
    <div class="logo">&#128222;</div>
    <h1><fmt:message key="app.title"/></h1>
</div>

<div class="card" style="max-width:500px">

    <div style="text-align:center;margin-bottom:20px">
        <div style="font-size:2.5rem;margin-bottom:10px">&#9993;</div>
        <h2 style="margin:0 0 8px 0">One step left!</h2>
        <p style="color:var(--color-text-muted);margin:0">
            Account <strong>${registeredLogin}</strong> created.
            Choose how to confirm it:
        </p>
    </div>

    <div class="confirm-methods">

        <%-- Email confirmation method --%>
        <div class="confirm-method">
            <h3>&#128140; By email</h3>
            <p>
                We sent a confirmation link to your email address.
                Open the letter and click the link.
            </p>
        </div>

        <%-- Telegram confirmation method (shown only if bot is configured) --%>
        <c:if test="${tgBotConfigured}">
            <div class="confirm-method">
                <h3>&#128526; Via Telegram bot</h3>
                <p>
                    Open the bot in Telegram and the confirmation will happen automatically
                    when you click Start (the token is embedded in the link).
                </p>
                <a class="tg-link"
                   href="https://t.me/${tgBotUsername}?start=${confirmationToken}"
                   target="_blank"
                   rel="noopener noreferrer">
                    &#128279; Open @${tgBotUsername} in Telegram
                </a>
                <br>
                <span style="color:var(--color-text-muted);font-size:.85rem">
                    Or send this command manually:
                </span>
                <br>
                <code class="tg-command" title="Click to copy" onclick="copyToken(this)">
                    /start ${confirmationToken}
                </code>
            </div>
        </c:if>

    </div>

    <div style="text-align:center;margin-top:28px">
        <a href="${pageContext.request.contextPath}/index.jsp" class="btn btn-outline">
            Back to login
        </a>
    </div>
</div>

<script>
    function copyToken(el) {
        var text = el.textContent.trim();
        navigator.clipboard.writeText(text).then(function () {
            var original = el.textContent;
            el.textContent = 'Copied!';
            setTimeout(function () { el.textContent = original; }, 1500);
        });
    }
</script>

</body>
</html>
