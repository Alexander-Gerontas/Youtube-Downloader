import com.yt.downloader.Downloader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DownloaderTest
{
    @Test
    public void successfullAudioDownload()
    {
        Downloader downloader = new Downloader("https://www.youtube.com/watch?v=Hcfbg994fu4");

        var mp3File = downloader.downloadByUrl();

        var videoTitle = downloader.getVideoTitle();

        Assertions.assertEquals(mp3File.getName(), videoTitle + ".mp3");

        mp3File.delete();
    }
}
