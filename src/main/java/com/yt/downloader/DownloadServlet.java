package com.yt.downloader;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

@WebServlet(name = "DownloadServlet", urlPatterns = "/download")
public class DownloadServlet extends HttpServlet
{
    private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        String url = request.getParameter("url");

        Downloader downloader = new Downloader(url);
        var file = downloader.downloadByUrl();

        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
        response.setContentType("audio/mpeg");

        // Prepare streams.
        BufferedInputStream input = null;
        BufferedOutputStream output = null;

        try
        {
            // Open streams.
            input = new BufferedInputStream(new FileInputStream(file), DEFAULT_BUFFER_SIZE);
            output = new BufferedOutputStream(response.getOutputStream(), DEFAULT_BUFFER_SIZE);

            // Write file contents to response.
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int length;
            while ((length = input.read(buffer)) > 0)
            {
                output.write(buffer, 0, length);
            }
        } finally
        {
            // delete the file from the server
            file.delete();

            // Gently close streams.
            input.close();
            output.close();
        }
    }
}