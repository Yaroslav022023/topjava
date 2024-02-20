<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:set var="baseI18nKeys" value="${'common.deleted,common.saved,common.errorStatus,common.confirm'}"/>
<c:choose>
    <c:when test="${not empty i18nKeys}">
        <c:set var="combinedI18nKeys" value="${baseI18nKeys},${i18nKeys}"/>
    </c:when>
    <c:otherwise>
        <c:set var="combinedI18nKeys" value="${baseI18nKeys}"/>
    </c:otherwise>
</c:choose>

<script type="text/javascript">
    const i18n = {};
    <c:forEach var="key" items="${fn:split(combinedI18nKeys, ',')}">
    i18n["${key.trim()}"] = "<spring:message code="${key.trim()}"/>";
    </c:forEach>
</script>
