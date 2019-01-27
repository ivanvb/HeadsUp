package com.example.headsup;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.List;

public class ExternalMessage {

    private static Context context;

    public static void sendTweet(Context context, String message)
    {
        String appResolveValue = "com.twitter.android";
        ExternalMessage.context = context;
        sendMessageToAnotherApplication(appResolveValue, message);
    }

    public static void sendWhatsappMessage(Context context, String message)
    {
        String appResolveValue = "com.whatsapp";
        ExternalMessage.context = context;
        sendMessageToAnotherApplication(appResolveValue, message);
    }

    private static void sendMessageToAnotherApplication(String applicationId, String message)
    {
        Intent tweetIntent = new Intent(Intent.ACTION_SEND);
        tweetIntent.putExtra(Intent.EXTRA_TEXT, message);
        tweetIntent.setType("text/plain");

        PackageManager packManager = context.getPackageManager();
        List<ResolveInfo> resolvedInfoList = packManager.queryIntentActivities(tweetIntent,  PackageManager.MATCH_DEFAULT_ONLY);

        boolean resolved = false;
        for(ResolveInfo resolveInfo: resolvedInfoList) {
            if (resolveInfo.activityInfo.packageName.startsWith(applicationId)) {
                tweetIntent.setClassName(
                        resolveInfo.activityInfo.packageName,
                        resolveInfo.activityInfo.name);
                resolved = true;
                break;
            }
            else
            {
                System.out.println(resolveInfo.activityInfo.packageName);
            }
        }
        if(resolved){
            context.startActivity(tweetIntent);
        }else{

        }
    }
}
