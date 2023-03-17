import com.yt.downloader.YT3;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class MyTest
{
    @Test
    @Disabled
    public void successfullConvertion2() throws IOException
    {
        YT3 yt = new YT3();

        yt.test1("123");
    }
}
