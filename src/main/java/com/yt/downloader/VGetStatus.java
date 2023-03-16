package com.yt.downloader;

import com.github.axet.vget.info.VideoFileInfo;
import com.github.axet.vget.info.VideoInfo;
import com.github.axet.vget.vhs.VimeoInfo;
import com.github.axet.vget.vhs.YouTubeInfo;
import com.github.axet.wget.SpeedInfo;
import com.github.axet.wget.info.DownloadInfo;
import com.github.axet.wget.info.DownloadInfo.Part;
import com.github.axet.wget.info.DownloadInfo.Part.States;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VGetStatus implements Runnable
{
    VideoInfo videoinfo;
    long last;

    Map<VideoFileInfo, SpeedInfo> map = new HashMap<VideoFileInfo, SpeedInfo>();

    public VGetStatus(VideoInfo i)
    {
        this.videoinfo = i;
    }

    public SpeedInfo getSpeedInfo(VideoFileInfo dinfo)
    {
        SpeedInfo speedInfo = map.get(dinfo);
        if (speedInfo == null)
        {
            speedInfo = new SpeedInfo();
            speedInfo.start(dinfo.getCount());
            map.put(dinfo, speedInfo);
        }
        return speedInfo;
    }

    @Override
    public void run()
    {
        List<VideoFileInfo> dinfoList = videoinfo.getInfo();

        // notify app or save download state
        // you can extract information from DownloadInfo info;
        switch (videoinfo.getState())
        {
            case EXTRACTING:
            case EXTRACTING_DONE:
            case DONE:
                if (videoinfo instanceof YouTubeInfo)
                {
                    YouTubeInfo i = (YouTubeInfo) videoinfo;
                    System.out.println(videoinfo.getState() + " " + i.getVideoQuality());
                } else if (videoinfo instanceof VimeoInfo)
                {
                    VimeoInfo i = (VimeoInfo) videoinfo;
                    System.out.println(videoinfo.getState() + " " + i.getVideoQuality());
                } else
                {
                    System.out.println("downloading unknown quality");
                }
                for (VideoFileInfo d : videoinfo.getInfo())
                {
                    SpeedInfo speedInfo = getSpeedInfo(d);
                    speedInfo.end(d.getCount());
                    System.out.println(String.format("file:%d - %s (%s)", dinfoList.indexOf(d), d.targetFile,
                            speedInfo.getAverageSpeed()));
                }
                break;
            case ERROR:
                System.out.println(videoinfo.getState() + " " + videoinfo.getDelay());

                if (dinfoList != null)
                {
                    for (DownloadInfo dinfo : dinfoList)
                    {
                        System.out.println("file:" + dinfoList.indexOf(dinfo) + " - " + dinfo.getException() + " delay:"
                                + dinfo.getDelay());
                    }
                }
                break;
            case RETRYING:
                System.out.println(videoinfo.getState() + " " + videoinfo.getDelay());

                if (dinfoList != null)
                {
                    for (DownloadInfo dinfo : dinfoList)
                    {
                        System.out.println("file:" + dinfoList.indexOf(dinfo) + " - " + dinfo.getState() + " "
                                + dinfo.getException() + " delay:" + dinfo.getDelay());
                    }
                }
                break;
            case DOWNLOADING:
                long now = System.currentTimeMillis();
                if (now - 1000 > last)
                {
                    last = now;

                    String parts = "";

                    for (VideoFileInfo dinfo : dinfoList)
                    {
                        SpeedInfo speedInfo = getSpeedInfo(dinfo);
                        speedInfo.step(dinfo.getCount());

                        List<Part> pp = dinfo.getParts();
                        if (pp != null)
                        {
                            // multipart download
                            for (Part p : pp)
                            {
                                if (p.getState().equals(States.DOWNLOADING))
                                {
                                    parts += String.format("part#%d(%.2f) ", p.getNumber(),
                                            p.getCount() / (float) p.getLength());
                                }
                            }
                        }
                        System.out.printf("file:%d - %s %.2f %s (%s)%n", dinfoList.indexOf(dinfo),
                                videoinfo.getState(), dinfo.getCount() / (float) dinfo.getLength(), parts,
                                speedInfo.getCurrentSpeed());
                    }
                }
                break;
            default:
                break;
        }
    }
}
