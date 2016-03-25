package io.gitcafe.maxco292.editsharp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

public class EditTextTest extends EditText {


    @Override
    protected void onCreateContextMenu(ContextMenu menu) {
        super.onCreateContextMenu(menu);
        menu.add("上传文件到服务器");
    }

    public EditTextTest(Context cn,AttributeSet as)
    {
        super(cn,as);
        setPadding(50, 0, 0, 0);
        setGravity(Gravity.TOP);
        setHorizontallyScrolling(true);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(getText().toString().length()!=0){
            float y;
            Paint p=new Paint();
            p.setColor(Color.GRAY);
            p.setTextSize(30);
            for(int l=0;l<getLineCount();l++){
                y=((l+1)*getLineHeight())-(getLineHeight()/4);
                canvas.drawText(String.valueOf(l+1),0,y,p);
                canvas.save();
            }
        }
            /*int k=getLineHeight();
            int i=getLineCount();
            canvas.drawLine(90,0,90,getHeight()+(i*k),line);
            int y=(getLayout().getLineForOffset(getSelectionStart())+1)*k;
            canvas.drawLine(0,y,getWidth(),y,line);
            canvas.save();
            canvas.restore();*/
        //super.onDraw(canvas);
    }

}