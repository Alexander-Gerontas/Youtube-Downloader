package com.yt.downloader;

import com.github.axet.vget.VGet;
import com.github.axet.vget.info.VGetParser;
import com.github.axet.vget.info.VideoFileInfo;
import com.github.axet.vget.info.VideoInfo;
import com.github.axet.vget.vhs.YouTubeMPGParser;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class YT
{
    YT2 yt2;

    public YT()
    {
        yt2 = new YT2();
    }

    public void test0() throws MalformedURLException
    {
//        String url = "https://www.youtube.com/watch?v=s10ARdfQUOY";
        String url = "https://www.youtube.com/watch?v=0dOmcdz-PN0";
//            String path = "D:\\Manindar\\YTD\\";
        String videoId = url.split("\\?v=")[1];
        String path = "mp4\\" + videoId;

        // download mp4 format only, fail if non exist
        VGetParser user = new YouTubeMPGParser();

        URL web = new URL(url);

        VideoInfo videoinfo = user.info(web);



        VGetStatus notify = new VGetStatus(videoinfo);

        System.out.println("Title: " + videoinfo.getTitle());
        List<VideoFileInfo> list = videoinfo.getInfo();
        if (list != null) {
            for (VideoFileInfo d : list) {
                // [OPTIONAL] setTarget file for each download source video/audio
                // use d.getContentType() to determine which or use
                // v.targetFile(dinfo, ext, conflict) to set name dynamically or
                // d.targetFile = new File("/Downloads/CustomName.mp3");
                // to set file name manually.
                System.out.println("Download URL: " + d.getSource());
            }
        }

        try
        {
            final AtomicBoolean stop = new AtomicBoolean(false);
//            VGet v = new VGet(new URL(url), new File( path));
            VGet v = new VGet(videoinfo, new File(path));
            v.download(user, stop, notify);
//            v.download();
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }

        File inputFile = new File(path);

        yt2.test1(inputFile);
    }
}
