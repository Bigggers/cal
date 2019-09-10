package com.example.demo;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.view.View;
import android.view.Window;
import android.graphics.Color;
import android.os.Bundle;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.widget.Button;
import android.widget.TextView;
public class MainActivity extends AppCompatActivity  implements View.OnClickListener{
    private TextView textView;
    private Button[] buttons = new Button[20];
    private int[] ids = new int[]{R.id.bt1,R.id.bt2,R.id.bt3,R.id.bt4,R.id.bt5,R.id.bt6,R.id.bt7,
            R.id.bt8,R.id.bt9,R.id.bt10,R.id.bt11,R.id.bt12,R.id.bt13,R.id.bt14,R.id.bt15,R.id.bt16,R.id.bt17,R.id.bt18,R.id.bt19,R.id.bt20
    };
    private String expression = "";
    private boolean end = false;
    private int count=2;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Resources res = getResources();
        Drawable drawable = res.getDrawable(R.drawable.bkcolor);
        this.getWindow().setBackgroundDrawable(drawable);
        textView = (TextView)findViewById(R.id.contentText);
        textView.setTextColor(Color.WHITE);
        for(int i=0;i<ids.length;i++){
            buttons[i] = (Button)findViewById(ids[i]);//
            buttons[i].setOnClickListener(this);
        }
    }
    public void onClick(View view)
    {
        int id = view.getId();
        Button button = (Button)view.findViewById(id);
        String current = button.getText().toString();
        if(end){ //算式结束，清零
            expression = "";
            end = false;
        }
        if(current.equals("AC"))
        {   //dianj
            expression = "0";
            count=0;
        }
        else if(current.equals("BIN"))
        {
            // int n=Integer.valueOf(expression);
            //expression=Integer.toBinaryString(n);
            try
            {
                int n=Integer.valueOf(expression);
                if(n==0)
                    expression="0";
                expression=Integer.toBinaryString(n);
            }
            catch (Exception e)
            {
                AlertDialog alertDialog1 = new AlertDialog.Builder(this)
                        .setTitle("warning")//标题
                        .setMessage("请输入整数")//内容
                        .setIcon(R.mipmap.ic_launcher)//图标
                        .create();
                alertDialog1.show();
                e.printStackTrace();
            }
        }
        else if(current.equals("HEX"))
        {
            try
            {
                int n=Integer.valueOf(expression);
                if(n==0)
                    expression="0";
                expression=Integer.toHexString(n);
            }
            catch (Exception e)
            {
                AlertDialog alertDialog1 = new AlertDialog.Builder(this)
                        .setTitle("warning")//标题
                        .setMessage("请输入整数")//内容
                        .setIcon(R.mipmap.ic_launcher)//图标
                        .create();
                alertDialog1.show();
                e.printStackTrace();
            }
        }
        else if(current.equals("BACK"))
        { //如果点击退格
            if(expression.length()>1)
            { //算式长度大于1
                expression = expression.substring(0,expression.length()-1);//退一格
                int i = expression.length()-1;
                char tmp = expression.charAt(i); //获得最后一个字符
                char tmpFront = tmp;
                for(;i>=0;i--){ //向前搜索最近的 +-*/和.，并退出
                    tmpFront = expression.charAt(i);
                    if(tmpFront=='.'||tmpFront=='+'||tmpFront=='-'||tmpFront=='*'||tmpFront=='/'){
                        break;
                    }
                }
                //    Toast.makeText(this, "tmp = "+tmp, Toast.LENGTH_SHORT).show();
                if(tmp>='0'&&tmp<='9'){ //最后一个字符为数字，则识别数赋值为0
                    count=0;
                }
                else if(tmp==tmpFront&&tmpFront!='.')
                    count=2; //如果为+-*/，赋值为2
                else if(tmpFront=='.')
                    count=1; //如果前面有小数点赋值为1
            }
            else if(expression.length()==1)
                expression = "0";
        }
        else if(current.equals("."))
        {
            if(expression.equals("")||count==2){
                expression+="0"+current;
                count = 1;  //小数点按过之后赋值为1
            }
            if(count==0){
                expression+=".";
                count = 1;
            }
        }
        else if(current.equals("+")||current.equals("-")||current.equals("*")||current.equals("/"))
        {
            if(count==0)
            {
                expression+=current;
                count = 2;  //  +-*/按过之后赋值为2
            }
        }
        else if(current.equals("="))
        {   //计算结果并显示
            double result = count();
            //expression+="="+result;
            expression=String.valueOf(result);
            end = true; //计算结束
        }
        else
        {//此处是当退格出现2+0时，用current的值替代0
            if(expression.length()>=1)
            {
                char tmp1 = expression.charAt(expression.length()-1);
                if(tmp1=='0'&&expression.length()==1)
                    expression = expression.substring(0,expression.length()-1);
                else if(tmp1=='0'&&expression.length()>1)
                {
                    char tmp2 = expression.charAt(expression.length()-2);
                    if(tmp2=='+'||tmp2=='-'||tmp2=='*'||tmp2=='/')
                        expression = expression.substring(0,expression.length()-1);
                }
            }
            expression+=current;
            if(count==2||count==1) count=0;
        }
        //    Toast.makeText(this, "count:"+count, Toast.LENGTH_SHORT).show();
        textView.setText(expression); //显示表达式
    }
    //解析表达式进行计算
    private double count()
    {
        double result=0;
        double tNum=1,lowNum=0.1,num=0;
        char tmp=0;
        int operate = 1; //识别+-*/，为+时为正数，为-时为负数，为×时为-2/2,为/时为3/-3;
        boolean point = false;
        for(int i=0;i<expression.length();i++)
        {   //遍历表达式
            tmp = expression.charAt(i);
            if(tmp=='.')
            { //判断是否有小数出现
                point = true;
                lowNum = 0.1;
            }
            else if(tmp=='+'||tmp=='-')
            {
                if(operate!=3&&operate!=-3)
                    //此处判断通用，适用于+-*
                    tNum *= num;
                else
                    //计算/
                    tNum /= num;
                if(operate<0) //累加入最终的结果
                    result -= tNum;
                else
                    result += tNum;
                operate = tmp=='+'?1:-1;
                num = 0;
                tNum = 1;
                point = false;
            }
            else if(tmp=='*')
            {
                if(operate!=3&&operate!=-3)
                    tNum *= num;
                else
                    tNum /= num;
                operate = operate<0?-2:2;
                point = false;
                num = 0;
            }
            else if(tmp=='/')
            {
                if(operate!=3&&operate!=-3)
                    tNum *= num;
                else
                    tNum /= num;
                operate = operate<0?-3:3;
                point = false;
                num = 0;
            }
            else{
                //读取expression中的每个数字，doube型
                if(!point){
                    num = num*10+tmp-'0';
                }else{
                    num += (tmp-'0')*lowNum;
                    lowNum*=0.1;
                }
            }
        }
        //循环遍历结束，计算最后一个运算符后面的数
        if(operate!=3&&operate!=-3)
            tNum *= num;
        else
            tNum /= num;
        //    Toast.makeText(this, "tNum = "+tNum, Toast.LENGTH_SHORT).show();
        if(operate<0)
            result -= tNum;
        else
            result += tNum;
        //返回最后的结果
        return result;
    }

}
