<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<script type="text/javascript">
  if (typeof i18n === 'undefined') {
    const i18n = {};
  }
  <c:forEach var="key" items='${i18nKeys}'>
  i18n["${key}"] = "<spring:message code="${key}"/>";
  </c:forEach>
</script>
