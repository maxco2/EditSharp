package io.gitcafe.maxco292.editsharp;
import io.gitcafe.maxco292.editsharp.CPP14.CPP14Lexer;
import io.gitcafe.maxco292.editsharp.JavaLang.JavaLexer;
import io.gitcafe.maxco292.editsharp.Lua.LuaLexer;
import io.gitcafe.maxco292.editsharp.Python3.Python3Lexer;
import io.gitcafe.maxco292.editsharp.adapter.MyAdapter;
import io.gitcafe.maxco292.editsharp.csharp.CSharp4Lexer;
import io.gitcafe.maxco292.editsharp.db.DatabaseAdapter;
import me.drakeet.materialdialog.MaterialDialog;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import android.widget.Toast;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.Lexer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import static java.lang.Character.isSpaceChar;

public class Editor extends AppCompatActivity {
    private String mPreValue[]={"0","1","2","3","4"};
    private EditText mtt;
    private ListView mLv;
    private FloatingActionButton fab_save;
    private CharSequence mCharS="NO STRING HERE NOW";
    private static boolean DELETE_MODIFY;
    private static boolean INSERT_MODIFY;
    private static boolean SUCCESS_MODEIFY;
    private static boolean AUTOCOMPLETE_MODIFY;
    private static boolean DBINSERT=true;
    public static int START;
    private static int MID_START;
    public static int END;
    private List<String> identifiers=new ArrayList<>();
    private List<String> testArray = new ArrayList<>();
    private MyAdapter adapter;
    private String typeoffile;
    private HashMap<String,Integer> typeID_HM=new HashMap<>();
    private String FilePath;
    private int toolbarheight;
    private int statusbarheight;
    private String FileName;
    private static final int UPLOAD_SUC=1;
    private static final int UPLOAD_FAIL=0;

    @SuppressLint("HandlerLeak")
    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case UPLOAD_SUC:
                    Toast.makeText(Editor.this,"已保存并上传文件到远程服务器，请在服务器端查看",Toast.LENGTH_SHORT).show();
                    break;
                case UPLOAD_FAIL:
                    Toast.makeText(Editor.this,"上传文件到远程服务器失败，请重试",Toast.LENGTH_SHORT).show();
                    break;
            }
            super.handleMessage(msg);
        }
    };
    private void setmPreValue(String[] str)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        str[0]=prefs.getString("remote_IP_text","");
        str[1]=prefs.getString("remote_Directory_text","");
        str[2]=prefs.getString("remote_username_text","");
        str[3]=prefs.getString("remote_passwords_text","");
        str[4]=prefs.getString("local_directory_text","");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setmPreValue(mPreValue);
        toolbarheight=dip2px(this,56);
        statusbarheight=getStatusBarHeight();
        typeID_HM.put("cs",107);
        typeID_HM.put("cpp",124);
        typeID_HM.put("py", 35);
        typeID_HM.put("c", 124);
        typeID_HM.put("h", 124);
        typeID_HM.put("lua", 50);
        typeID_HM.put("java", 100);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fab_save= (FloatingActionButton) findViewById(R.id.fab_save);

        SUCCESS_MODEIFY=true;
        adapter = new MyAdapter(this, testArray);
        Intent mintent=getIntent();
        Uri uri=Uri.parse(mintent.getStringExtra("filedata"));
        FilePath=uri.getPath();
        fab_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File fi = new File(FilePath);
                FileOutputStream testfileio;
                try {
                    testfileio = new FileOutputStream(fi);
                    byte b[]=mtt.getText().toString().getBytes();
                    testfileio.write(b,0,b.length);
                    testfileio.close();
                    Toast.makeText(Editor.this,"保存成功",Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        FileName=uri.getLastPathSegment();
        setTitle(FileName);
        mLv= (ListView) findViewById(R.id.list_all);
        mtt=(EditText)findViewById(R.id.note1);
        TextWatcher tw=new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d("---***---", "beforeTextChanged 被执行----> s=" + s+"----start="+ start
                        + "----after="+after + "----count" +count);
                if(DBINSERT)//run only once
                {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            DatabaseAdapter.getIntance(Editor.this).inserInfo(identifiers);
                            //Toast.makeText(Editor.this,"DATABASEUPDATE COMPLETED",Toast.LENGTH_SHORT).show();
                            Log.d("DBCOMPLETE","hahahahahaha");
                        }
                    }).start();
                    DBINSERT=false;
                }
                if(count>=1)
                {
                    DELETE_MODIFY=true;
                    INSERT_MODIFY=false;
                    START=(start-count-1)>=0?start-count-1:0;
                    MID_START=start;
                    END=start;
                    int length=s.length();
                    try {
                        char st = s.charAt(START);
                        char ed = s.charAt(END);
                        if (START != 0) {
                            while (!isSpaceChar(st) && st != '\n' && st != '\r' && START != 0) {
                                START -= 1;
                                st = s.charAt(START);
                            }
                            if (START != 0) {
                                START += 1;
                            }
                        }
                        while (!isSpaceChar(ed) && ed != '\n' && ed != '\r' && END != length) {
                            //未执行边界检查
                            END += 1;
                            ed = s.charAt(END);
                        }
                        //mCharS = s.subSequence(START, END);
                    }catch(Exception e)
                    {
                        mCharS="";
                    }
                    Log.d("bfc//////","START:"+START+"MID_START:"+MID_START);
                }
                else {
                    DELETE_MODIFY = false;
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("---***---", "onTextChanged 被执行--------start=" + start
                        + "\n----before=" + before + "\n----count" + count);
                if(!DELETE_MODIFY || AUTOCOMPLETE_MODIFY) {
                    if (count == 1) {
                        SUCCESS_MODEIFY = true;
                        START = start;
                        MID_START = start;
                        END = start + count;
                        INSERT_MODIFY = true;
                        DELETE_MODIFY = false;
                        //SUCCESS_MODEIFY = true;
                        //判断start是否为空
                        try {
                            char st = s.charAt(START);
                            char ed = s.charAt(END);
                            if (MID_START != 0) {
                                while (!isSpaceChar(st) && st != '\n' && st != '\r' && START != 0) {
                                    START -= 1;
                                    st = s.charAt(START);
                                }
                                if (START != 0) {
                                    START += 1;
                                }
                            }
                            //判断end是否为空
                            while (!isSpaceChar(ed) && ed != '\n' && ed != '\r' && END != s.length()) {
                                //未执行边界检查
                                END += 1;
                                ed = s.charAt(END);
                            }
                            mCharS = s.subSequence(START, MID_START + 1);
                        }catch (IndexOutOfBoundsException e)
                        {
                            e.printStackTrace();
                            mCharS="";
                        }
                        //Toast.makeText(Editor.this,mCharS,Toast.LENGTH_SHORT).show();
                    } else if (count > 1) {
                        SUCCESS_MODEIFY = true;
                        INSERT_MODIFY = true;
                        DELETE_MODIFY = false;
                        START = MID_START = start;
                        END = START + count;
                        char st = s.charAt(START);
                        char ed = s.charAt(END);
                        if (MID_START != 0) {
                            while (!isSpaceChar(st) && st != '\n' && st != '\r' && START != 0) {
                                START -= 1;
                                st = s.charAt(START);
                            }
                            if (START != 0) {
                                START += 1;
                            }
                        }
                        //判断end是否为空
                        int length = s.length();
                        while (!isSpaceChar(ed) && ed != '\n' && ed != '\r' && END != length) {
                            //未执行边界检查
                            END += 1;
                            ed = s.charAt(END);
                        }
                        mCharS = s.subSequence(START, MID_START + 1);
                    }
                }
                else
                {
                    INSERT_MODIFY=false;
                    SUCCESS_MODEIFY=true;
                    //Log.d("DELETE",s.toString());
                }

                try {
                    Log.d("前前后后", mCharS.toString());
                    Log.d("---ontcstr---", s.subSequence(START, END).toString());

                    //起始及结束位置，test专用
                    //Toast.makeText(Editor.this,"START:"+START+"*** END:"+END,Toast.LENGTH_SHORT).show();
                }
                catch (Exception e)
                {
                    Log.d("error:",e.getMessage());
                    Log.d("---ontc---","捕捉失败");
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("---***---", "afterTextChanged 被执行----");
//listview测试
                mtt.removeTextChangedListener(this);
                if(INSERT_MODIFY) {
                    try {
                        int pos = mtt.getSelectionStart();
                        Layout layout = mtt.getLayout();
                        int line = layout.getLineForOffset(pos);
                        int baseline = layout.getLineBaseline(line);
                        int ascent = layout.getLineAscent(line);
                        float x = layout.getPrimaryHorizontal(pos);
                        float y = baseline + ascent;

                        testArray = new ArrayList<>();
                        testArray = DatabaseAdapter.getIntance(Editor.this)
                                .queryInfo(
                                        mCharS.toString());
                        adapter.refreshData(testArray);
                        mLv.setAdapter(adapter);
                        mLv.setBackgroundColor(Color.WHITE);
                        mLv.setX(x);
                        mLv.setY((y + toolbarheight + statusbarheight+ (baseline / line)*2));//A little tricky here!
                        mLv.setVisibility(View.VISIBLE);
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
//
                if(SUCCESS_MODEIFY && START!=END) {
                    try {
                        Color_DFA(s, START, END);
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                mtt.addTextChangedListener(this);
            }
        };
        typeoffile=mintent.getStringExtra("type");
        try {
            fileOpen(typeoffile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mtt.addTextChangedListener(tw);
        mLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AUTOCOMPLETE_MODIFY = true;
                mtt.getText().insert(START + mCharS.length(), adapter.getItem(position));
                mtt.getText().delete(START, mCharS.length() + START - 1);
                mLv.setVisibility(View.GONE);
                AUTOCOMPLETE_MODIFY = false;
            }
        });

    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private void fileOpen(String typeoffile) throws IOException {
        File fi = new File(FilePath);
        FileInputStream testfileio = new FileInputStream(fi);
        byte b[]=new byte[testfileio.available()];
        testfileio.read(b);
        String ll=new String(b);
        SpannableString lll=Color_DFA(ll, typeoffile);
        mtt.setText(lll);
        testfileio.close();
    }
    private SpannableString Color_DFA(String input_str,String typeoffile) //全文自动机
    {
        SpannableString lll=new SpannableString(input_str);
        Lexer cs=selectlexer(typeoffile,input_str);
        CommonToken cst= (CommonToken) cs.nextToken();
        String gt=cst.getText();
        int type=cst.getType();
        Pattern allletter=Pattern.compile("[A-Za-z]*");
        int ID_NUM=typeID_HM.get(typeoffile);
        while(!Objects.equals(gt, "<EOF>")) {
            if(type==ID_NUM) {
                lll.setSpan(new ForegroundColorSpan(Color.RED), cst.getStartIndex(), cst.getStopIndex() + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                identifiers.add(gt);
            }else
            {

                if(allletter.matcher(gt).matches())
                {
                    lll.setSpan(new ForegroundColorSpan(Color.BLUE), cst.getStartIndex(), cst.getStopIndex() + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
            cst= (CommonToken) cs.nextToken();
            gt=cst.getText();
            type=cst.getType();
        }
        return lll;
    }
    public  void Color_DFA(Editable s,int st,int ed) //小型自动机
    {
        START=st;
        END=ed;
        String tempstr=s.toString().substring(START,END);
        SpannableString lll=new SpannableString(tempstr);
        Lexer cs=selectlexer(typeoffile,tempstr);
        CommonToken cst= (CommonToken) cs.nextToken();
        String gt=cst.getText();
        int type=cst.getType();
        Pattern allletter=Pattern.compile("[A-Za-z]*");
        int ID_NUM=typeID_HM.get(typeoffile);
        while(!Objects.equals(gt, "<EOF>")) {
            if(type==ID_NUM) {
                lll.setSpan(new ForegroundColorSpan(Color.RED), cst.getStartIndex(), cst.getStopIndex() + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                identifiers.add(gt);
            }else
            {
                if(allletter.matcher(gt).matches())
                {
                    lll.setSpan(new ForegroundColorSpan(Color.BLUE), cst.getStartIndex(), cst.getStopIndex() + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
            cst= (CommonToken) cs.nextToken();
            gt=cst.getText();
            type=cst.getType();
        }
        s.replace(START, END, lll);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(Objects.equals("上传文件到服务器", item.toString()))
        {
            final MaterialDialog materialDialog=new MaterialDialog(Editor.this);
            materialDialog.setTitle("上传服务器并保存文件？")
                    .setPositiveButton("是", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    File fi = new File(FilePath);
                                    FileOutputStream testfileio;
                                    try {
                                        testfileio = new FileOutputStream(fi);
                                        byte b[] = mtt.getText().toString().getBytes();
                                        testfileio.write(b, 0, b.length);
                                        testfileio.close();
                                        //Toast.makeText(Editor.this,"保存成功",Toast.LENGTH_SHORT).show();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    try {
                                        SSFTPsync.sshSftp(
                                                mPreValue[0],
                                                mPreValue[2],
                                                mPreValue[3],
                                                -1,
                                                mPreValue[1],
                                                FilePath,
                                                FileName
                                        );
                                        mHandler.obtainMessage(UPLOAD_SUC).sendToTarget();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        mHandler.obtainMessage(UPLOAD_FAIL).sendToTarget();
                                    }

                                }
                            }).start();
                            materialDialog.dismiss();
                        }
                    }).setNegativeButton("否", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    materialDialog.dismiss();
                }
            });
            materialDialog.show();

        }
        return super.onContextItemSelected(item);
    }

    private Lexer selectlexer(String typeoffile,String input_str)
    {
        Lexer cs;
        switch (typeoffile)
        {
            case "cs":
                cs=new CSharp4Lexer(new ANTLRInputStream(input_str));
                break;
            case "cpp":
                cs=new CPP14Lexer(new ANTLRInputStream(input_str));
                break;
            case "py":
                cs=new Python3Lexer(new ANTLRInputStream(input_str));
                break;
            case "java":
                cs=new JavaLexer(new ANTLRInputStream(input_str));
                break;
            case "c":
                cs=new CPP14Lexer(new ANTLRInputStream(input_str));
                break;
            case "lua":
                cs=new LuaLexer(new ANTLRInputStream(input_str));
                break;
            default://.h CPP/C headers
                cs=new CPP14Lexer(new ANTLRInputStream(input_str));
        }
        return cs;
    }

}
