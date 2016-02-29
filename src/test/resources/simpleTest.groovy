_writer.write("<!DOCTYPE html>\n<head><title>")
_writer.write("groovy")
_writer.write("</title></head><html>");
if (binding.hasVariable("body")) {
    _writer.write("<body>")
    _writer.write(body);
    _writer.write("</body>")
}
_writer.write("</html>")