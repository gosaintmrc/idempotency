<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Token幂等</title>
</head>
<body>
<form action="/idemo/trans" method="post">
    <input type="hidden" id="token" name="token" value="${token}">
    name: <input id="name" name="name" />
    <p>age:  <input id="age" name="age" />
    <p><input type="submit" value="submit" />
</form>
</body>
<script type="text/javascript">
    var value = document.getElementById("token").value;
    alert(value);
</script>
</html>