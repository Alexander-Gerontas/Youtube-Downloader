package com.yt.downloader;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@WebServlet(name = "DownloadServlet", urlPatterns = "/download")
public class DownloadServlet extends HttpServlet
{
    private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException
    {
        String url = request.getParameter("url");

        YT3 yt3 = new YT3();
        var file = yt3.test1(url);

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