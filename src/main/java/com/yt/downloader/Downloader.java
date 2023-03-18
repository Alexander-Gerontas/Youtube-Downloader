package com.yt.downloader;

import com.github.kiulian.downloader.YoutubeDownloader;
import com.github.kiulian.downloader.downloader.YoutubeProgressCallback;
import com.github.kiulian.downloader.downloader.request.RequestVideoFileDownload;
import com.github.kiulian.downloader.downloader.request.RequestVideoInfo;
import com.github.kiulian.downloader.downloader.response.Response;
import com.github.kiulian.downloader.model.videos.VideoDetails;
import com.github.kiulian.downloader.model.videos.VideoInfo;
import com.github.kiulian.downloader.model.videos.formats.AudioFormat;
import ws.schild.jave.Encoder;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;

import java.io.File;
import java.util.List;

public class Downloader
{
    private YoutubeDownloader youtubeDownloader;
    private VideoDetails videoDetails;
    private VideoInfo videoInfo;
    private String videoId;

    public Downloader(String inputFile)
    {
        // init downloader with default config
        youtubeDownloader = new YoutubeDownloader();
        videoId = inputFile.split("\\?v=")[1];

        RequestVideoInfo request = new RequestVideoInfo(videoId);
        Response<VideoInfo> response = youtubeDownloader.getVideoInfo(request);

        videoInfo = response.data();
        videoDetails = videoInfo.details();
    }

    public String getVideoTitle() {
        return videoDetails.title();
    }

    public File downloadByUrl()
    {
        // get audio formats
        List<AudioFormat> audioFormats = videoInfo.audioFormats();

        var format = audioFormats.stream()
                .filter(audio -> audio.audioQuality().name().equals("medium"))
                .findFirst()
                .orElse(audioFormats.get(0));

        // create new audiofile
        File filename = new File("src/main/resources/mp4/");

        // async downloading with callback
        RequestVideoFileDownload videoFileDownload = new RequestVideoFileDownload(format)
                .callback(new YoutubeProgressCallback<>()
                {
                    @Override
                    public void onDownloading(int progress)
                    {
                        System.out.printf("Downloaded %d%%\n", progress);
                    }

                    @Override
                    public void onFinished(File videoInfo)
                    {
                        System.out.println("Finished file: " + videoInfo);
                    }

                    @Override
                    public void onError(Throwable throwable)
                    {
                        System.out.println("Error: " + throwable.getLocalizedMessage());
                    }
                })
                .saveTo(filename)
                .renameTo(videoId);

        Response<File> downloadVideoFileResponse = youtubeDownloader.downloadVideoFile(videoFileDownload);

        var videoFile = downloadVideoFileResponse.data(); // will block current thread
        var audioFile = convertVideo(videoFile, videoDetails.title());

        // delete video file
        videoFile.delete();

        return audioFile;
    }

    public File convertVideo(File source, String title)
    {
        try
        {
            File target = new File("src/main/resources/mp3/" + title + ".mp3");

            //Audio Attributes
            AudioAttributes audio = new AudioAttributes();
            audio.setCodec("libmp3lame");
            audio.setBitRate(128000);
            audio.setChannels(2);
            audio.setSamplingRate(44100);

            //Encoding attributes
            EncodingAttributes attrs = new EncodingAttributes();
            attrs.setOutputFormat("mp3");
            attrs.setAudioAttributes(audio);

            //Encode
            Encoder encoder = new Encoder();
            encoder.encode(new MultimediaObject(source), target, attrs);

            return target;

        } catch (Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }
}
