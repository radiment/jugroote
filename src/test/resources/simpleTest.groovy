_writer.write("<!DOCTYPE html>\n<html>")
if (binding.hasVariable("body")) {
    _writer.write("<body>")
    _writer.write(body);
    _writer.write("</body>")
}
_writer.write("</html>")