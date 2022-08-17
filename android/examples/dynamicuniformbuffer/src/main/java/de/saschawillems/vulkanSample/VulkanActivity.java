/*
 * Copyright (C) 2018 by Sascha Willems - www.saschawillems.de
 *
 * This code is licensed under the MIT license (MIT) (http://opensource.org/licenses/MIT)
 */
package de.saschawillems.vulkanSample;

import static android.view.WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;

import android.app.AlertDialog;
import android.app.NativeActivity;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.concurrent.Semaphore;

public class VulkanActivity extends NativeActivity {

    static {
        // Load native library
        System.loadLibrary("native-lib");
    }

    EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout linearLayout = new LinearLayout(this);
        editText = new EditText(this);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setHint("请输入显示的个数：");
        Button button = new Button(this);
        button.setText("确定");
        linearLayout.addView(editText);
        linearLayout.addView(button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeDisplayCount();
            }
        });

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.width = 800;
        layoutParams.height = 200;
       layoutParams.gravity =  Gravity.RIGHT|Gravity.BOTTOM;
        layoutParams.format = PixelFormat.TRANSPARENT;




        getWindowManager().addView(linearLayout, layoutParams);//view即为需要添加的控件，layoutParams为参数
    }


    private void ChangeDisplayCount(){

      String inputText =  editText.getText().toString().trim();

      if(!TextUtils.isEmpty(inputText)){
          int count  = Integer.parseInt(inputText);
          if(count>0){
           JniInterface.ChangeDisplayCount(count);
          }
      }
    }

    // Use a semaphore to create a modal dialog

    private final Semaphore semaphore = new Semaphore(0, true);

    public void showAlert(final String message)
    {
        final VulkanActivity activity = this;

        ApplicationInfo applicationInfo = activity.getApplicationInfo();
        final String applicationName = applicationInfo.nonLocalizedLabel.toString();

        this.runOnUiThread(new Runnable() {
           public void run() {
               AlertDialog.Builder builder = new AlertDialog.Builder(activity, android.R.style.Theme_Material_Dialog_Alert);
               builder.setTitle(applicationName);
               builder.setMessage(message);
               builder.setPositiveButton("Close", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       semaphore.release();
                   }
               });
               builder.setCancelable(false);
               AlertDialog dialog = builder.create();
               dialog.show();
           }
        });
        try {
            semaphore.acquire();
        }
        catch (InterruptedException e) { }
    }
}
