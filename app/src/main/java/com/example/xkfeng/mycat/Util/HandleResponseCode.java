package com.example.xkfeng.mycat.Util;

import android.content.Context;
import android.content.Intent;
//https://www.cnblogs.com/linjzong/p/4211661.html
//android 手把手教你做图片处理
import com.example.xkfeng.mycat.Activity.LoginActivity;

public class HandleResponseCode {

    public static void onHandle(Context context, int status) {
        switch (status) {
            case 0:
                break;
            case 1000:
                ITosast.showShort(context, "mycat_record_voice_permission_denied")
                        .show();

                break;
            case 1001:
                ITosast.showShort(context, "mycat_local_picture_not_found_toast")
                        .show();
                break;
            case 1002:
                ITosast.showShort(context, "mycat_user_already_exist_toast").show();
                break;
            case 1003:
                ITosast.showShort(context, "mycat_illegal_state_toast").show();
                break;
            case 800002:
                ITosast.showShort(context, "mycat_server_800002").show();
                break;
            case 800003:
                ITosast.showShort(context, "mycat_server_800003").show();
                break;
            case 800004:
                ITosast.showShort(context, "mycat_server_800004").show();
                break;
            case 800005:
                ITosast.showShort(context, "mycat_server_800005").show();
                break;
            case 800006:
                ITosast.showShort(context, "mycat_server_800006").show();
                break;
            case 800012:
                ITosast.showShort(context, "mycat_server_800012").show();
                break;
            case 800013:
                ITosast.showShort(context, "mycat_server_800013").show();
                Intent intent = new Intent();
                intent.setClass(context, LoginActivity.class);
                context.startActivity(intent);
                break;
            case 800014:
                ITosast.showShort(context, "mycat_server_800014").show();
                break;
            case 801001:
            case 802001:
                ITosast.showShort(context, "mycat_server_802001").show();
                break;
            case 802002:
            case 898002:
            case 801003:
            case 899002:
                ITosast.showShort(context, "mycat_server_801003").show();
                break;
            case 899004:
            case 801004:
                ITosast.showShort(context, "mycat_server_801004").show();
                break;
            case 803001:
                ITosast.showShort(context, "mycat_server_803001").show();
                break;
            case 803002:
                ITosast.showShort(context, "mycat_server_803002").show();
                break;
            case 803003:
                ITosast.showShort(context, "mycat_server_803003").show();
                break;
            case 803004:
                ITosast.showShort(context, "mycat_server_803004").show();
                break;
            case 803005:
                ITosast.showShort(context, "mycat_server_803005").show();
                break;
            case 803008:
                ITosast.showShort(context, "mycat_server_803008").show();
                break;
            case 803009:
                ITosast.showShort(context, "mycat_server_803009").show();
                break;
            case 803010:
                ITosast.showShort(context, "mycat_server_803010").show();
                break;
            case 805002:
                ITosast.showShort(context, "").show();
            case 808003:
                ITosast.showShort(context, "mycat_server_808003").show();
                break;
            case 808004:
                ITosast.showShort(context, "mycat_server_808004").show();
                break;
            case 810003:
                ITosast.showShort(context, "mycat_server_810003").show();
                break;
            case 810005:
                ITosast.showShort(context, "mycat_server_810005").show();
                break;
            case 810007:
                ITosast.showShort(context, "mycat_server_810007").show();
                break;
            case 810008:
                ITosast.showShort(context, "mycat_server_810008").show();
                break;
            case 810009:
                ITosast.showShort(context, "mycat_server_810009").show();
                break;
            case 811003:
                ITosast.showShort(context, "mycat_server_811003").show();
                break;
            case 812002:
                ITosast.showShort(context, "mycat_server_812002").show();
                break;
            case 818001:
                ITosast.showShort(context, "mycat_server_818001").show();
                break;
            case 818002:
                ITosast.showShort(context, "mycat_server_818002").show();
                break;
            case 818003:
                ITosast.showShort(context, "mycat_server_818003").show();
                break;
            case 818004:
                ITosast.showShort(context, "mycat_server_818004").show();
                break;
            case 899001:
            case 898001:
                ITosast.showShort(context, "mycat_sdk_http_899001").show();
                break;
            case 898005:
                ITosast.showShort(context, "mycat_sdk_http_898005").show();
                break;
            case 898006:
                ITosast.showShort(context, "mycat_sdk_http_898006").show();
                break;
            case 898008:
                ITosast.showShort(context, "mycat_sdk_http_898008").show();
                break;
            case 898009:
                ITosast.showShort(context, "mycat_sdk_http_898009").show();
                break;
            case 898010:
                ITosast.showShort(context, "mycat_sdk_http_898010").show();
                break;
            case 898030:
                ITosast.showShort(context, "mycat_sdk_http_898030").show();
                break;
            case 800009:
            case 871104:
                ITosast.showShort(context, "mycat_sdk_87x_871104").show();
                break;
            case 871300:
                ITosast.showShort(context, "mycat_sdk_87x_871300").show();
                break;
            case 871303:
                ITosast.showShort(context, "mycat_sdk_87x_871303").show();
                break;
            case 871304:
                ITosast.showShort(context, "mycat_sdk_87x_871304").show();
                break;
            case 871305:
                ITosast.showShort(context, "mycat_sdk_87x_871305").show();
                break;
            case 871309:
                ITosast.showShort(context, "mycat_sdk_87x_871309").show();
                break;
            case 871310:
                ITosast.showShort(context, "mycat_sdk_87x_871310").show();
                break;
            case 871311:
                ITosast.showShort(context, "mycat_sdk_87x_871311").show();
                break;
            case 871312:
                ITosast.showShort(context, "mycat_sdk_87x_871312").show();
                break;
            case 871319:
                ITosast.showShort(context, "mycat_sdk_87x_871319").show();
                break;
            case 871403:
                ITosast.showShort(context, "mycat_sdk_87x_871403").show();
                break;
            case 871404:
                ITosast.showShort(context, "mycat_sdk_87x_871404").show();
                break;
            case 871501:
                ITosast.showShort(context, "mycat_sdk_87x_871501").show();
                break;
            case 871502:
                ITosast.showShort(context, "mycat_sdk_87x_871502").show();
                break;
            case 871503:
                ITosast.showShort(context, "mycat_sdk_87x_871503").show();
                break;
            case 871504:
                ITosast.showShort(context, "mycat_sdk_87x_871504").show();
                break;
            case 871505:
                ITosast.showShort(context, "mycat_sdk_87x_871505").show();
                break;
            case 871506:
                ITosast.showShort(context, "mycat_sdk_87x_871506").show();
                break;
            case 871102:
            case 871201:
                ITosast.showShort(context, "mycat_sdk_87x_871201").show();
                break;
            default:
                ITosast.showShort(context, "mycat_sdk_default").show();
                break;
        }

    }
}
