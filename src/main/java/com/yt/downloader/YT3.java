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

public class YT3
{
    YoutubeDownloader downloader;

    public YT3()
    {
        // init downloader with default config
        downloader = new YoutubeDownloader();
    }

    public File test1(String inputFile)
    {
        String videoId = inputFile.split("\\?v=")[1];

        // sync parsing
        RequestVideoInfo request = new RequestVideoInfo(videoId);
        Response<VideoInfo> response = downloader.getVideoInfo(request);

        VideoInfo video = response.data();

        VideoDetails details = video.details();
        System.out.println(details.title());

        // get audio formats
        List<AudioFormat> audioFormats = video.audioFormats();
        audioFormats.forEach(it ->
        {
            System.out.println(it.audioQuality() + " : " + it.url());
        });

        var format = audioFormats.stream()
                .filter(audio -> audio.audioQuality().equals("medium"))
                .findFirst()
                .orElse(audioFormats.get(0));

//        File filename = new File("mp4/" + videoId);

        File filename = new File("src/main/resources/mp4/");

//        resourcesloader.class.getClassLoader().getResource("package1/resources/repository/SSL-Key/cert.jks").toString();

        // async downloading with callback
        RequestVideoFileDownload request1 = new RequestVideoFileDownload(format)
                .callback(new YoutubeProgressCallback<File>()
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

        Response<File> response1 = downloader.downloadVideoFile(request1);

        var videoFile =  response1.data(); // will block current thread

        var audioFile = convertVideo(videoFile, details.title());

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
