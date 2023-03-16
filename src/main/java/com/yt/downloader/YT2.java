package com.yt.downloader;

import com.cloudmersive.client.AudioApi;
import com.cloudmersive.client.invoker.ApiClient;
import com.cloudmersive.client.invoker.ApiException;
import com.cloudmersive.client.invoker.Configuration;
import com.cloudmersive.client.invoker.auth.ApiKeyAuth;

import java.io.File;

public class YT2
{
    YT2()
    {

    }

    public void test1(File inputFile)
    {

        ApiClient defaultClient = Configuration.getDefaultApiClient();
        ApiKeyAuth Apikey = (ApiKeyAuth) defaultClient.getAuthentication("Apikey");
        Apikey.setApiKey("53ea4273-cf3a-46b4-b50e-240ea0ce30e4");

        Integer bitRate = 56;

        AudioApi apiInstance = new AudioApi();

        try {

            byte[] result = apiInstance.audioConvertToMp3(inputFile, "", bitRate);

            System.out.println(result);

        } catch (ApiException e) {

            System.err.println("Exception when calling AudioApi#audioConvertToMp3");

            e.printStackTrace();

        }
    }
}
