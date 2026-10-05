package com.schoolteacherassistant

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private val prefs by lazy { getSharedPreferences("teacher_assistant", MODE_PRIVATE) }
    private lateinit var content: FrameLayout
    private var selectedClass = "10A"
    private val absent = mutableSetOf<Int>()
    data class Student(val roll:Int,val name:String)

    override fun onCreate(b:Bundle?) {
        super.onCreate(b); seed(); shell(); attendance()
    }

    private fun school() = prefs.getString("school","YOUR SCHOOL NAME") ?: "YOUR SCHOOL NAME"
    private fun classes():List<String> {
        val a=JSONArray(prefs.getString("classes","[\"10A\",\"10B\",\"9A\"]"))
        return (0 until a.length()).map{a.getString(it)}
    }
    private fun students(c:String):MutableList<Student>{
        val a=JSONArray(prefs.getString("students_"+c,"[]"))
        return (0 until a.length()).map{val o=a.getJSONObject(it);Student(o.getInt("roll"),o.getString("name"))}.toMutableList()
    }
    private fun saveStudents(c:String,list:List<Student>){
        val a=JSONArray();list.sortedBy{it.roll}.forEach{a.put(JSONObject().put("roll",it.roll).put("name",it.name))}
        prefs.edit().putString("students_"+c,a.toString()).apply()
    }
    private fun seed(){
        if(!prefs.contains("students_10A"))saveStudents("10A",listOf(Student(1,"Rahul Kumar"),Student(2,"Aman Kumar"),Student(3,"Rohit Kumar"),Student(4,"Aditya Kumar"),Student(5,"Priya Kumari")))
        if(!prefs.contains("students_10B"))saveStudents("10B",listOf(Student(1,"Arjun Kumar"),Student(2,"Neha Kumari"),Student(3,"Vivek Kumar")))
        if(!prefs.contains("students_9A"))saveStudents("9A",listOf(Student(1,"Student One"),Student(2,"Student Two")))
    }

    private fun shell(){
        val root=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setBackgroundColor(getColor(R.color.bg))}
        val h=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(20,18,20,10)}
        h.addView(TextView(this).apply{text="School Teacher Assistant";textSize=23f;setTextColor(getColor(R.color.text));setTypeface(typeface,1)})
        h.addView(TextView(this).apply{text=date();textSize=13f;setTextColor(0xff687386.toInt())})
        content=FrameLayout(this).apply{layoutParams=LinearLayout.LayoutParams(-1,0,1f)}
        val nav=LinearLayout(this).apply{orientation=LinearLayout.HORIZONTAL;setPadding(8,8,8,12);setBackgroundColor(0xffffffff.toInt())}
        nav.addView(btn("📋\nAttendance"){attendance()});nav.addView(btn("🤖\nAI Notice"){notice()});nav.addView(btn("⚙\nSettings"){settings()})
        root.addView(h);root.addView(content);root.addView(nav);setContentView(root)
    }
    private fun btn(t:String,click:()->Unit)=MaterialButton(this).apply{text=t;isAllCaps=false;layoutParams=LinearLayout.LayoutParams(0,70,1f).apply{setMargins(4,0,4,0)};setOnClickListener{click()}}
    private fun card()=MaterialCardView(this).apply{radius=18f;cardElevation=2f;setCardBackgroundColor(0xffffffff.toInt());layoutParams=LinearLayout.LayoutParams(-1,-2).apply{setMargins(16,8,16,8)}}
    private fun scroll():Pair<ScrollView,LinearLayout>{val s=ScrollView(this);val b=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(0,4,0,20)};s.addView(b);return Pair(s,b)}

    private fun attendance(){
        absent.clear();val p=scroll();val s=p.second;val st=students(selectedClass)
        val c=card();val l=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(18,18,18,18)}
        l.addView(TextView(this).apply{text="📋  Class Attendance Report";textSize=20f;setTypeface(typeface,1);setTextColor(getColor(R.color.text))})
        l.addView(TextView(this).apply{text="Select absent students. Counts are automatic.";textSize=13f;setTextColor(0xff687386.toInt());setPadding(0,5,0,8)})
        l.addView(MaterialButton(this).apply{text="Class: "+selectedClass+"  ▾";isAllCaps=false;setOnClickListener{chooseClass()}})
        c.addView(l);s.addView(c)
        val list=card();val box=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(18,10,18,10)}
        box.addView(TextView(this).apply{text="Absent Students ("+st.size+" total)";textSize=17f;setTypeface(typeface,1)})
        st.forEach{q->box.addView(CheckBox(this).apply{text="Roll "+q.roll+"  •  "+q.name;textSize=16f;setPadding(0,8,0,8);setOnCheckedChangeListener{_,v->if(v)absent.add(q.roll)else absent.remove(q.roll)}})}
        list.addView(box);s.addView(list)
        s.addView(MaterialButton(this).apply{text="SEND ATTENDANCE REPORT → WhatsApp";isAllCaps=false;setOnClickListener{shareAttendance(st)}},LinearLayout.LayoutParams(-1,58).apply{setMargins(16,12,16,8)})
        content.removeAllViews();content.addView(p.first)
    }

    private fun shareAttendance(st:List<Student>){
        val a=st.filter{absent.contains(it.roll)};val total=st.size;val present=total-a.size
        val names=if(a.isEmpty())"None"else a.sortedBy{it.roll}.mapIndexed{i,x->(i+1).toString()+". Roll No. "+x.roll+" - "+x.name}.joinToString("\n")
        val msg=school()+"\n\n📋 CLASS ATTENDANCE REPORT\n\n📅 Date: "+date()+"\n🏫 Class: "+selectedClass+"\n\n👥 Total Students: "+total+"\n✅ Present Students: "+present+"\n❌ Absent Students: "+a.size+"\n\nAbsent Students:\n"+names
        whatsapp(msg)
    }

    private fun notice(){
        val p=scroll();val s=p.second;val c=card();val l=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(18,18,18,18)}
        l.addView(TextView(this).apply{text="🤖  AI Notice Generator";textSize=20f;setTypeface(typeface,1)})
        l.addView(TextView(this).apply{text="Enter a topic or rough instruction.";textSize=13f;setTextColor(0xff687386.toInt())})
        val input=EditText(this).apply{hint="Parent-teacher meeting tomorrow at 10 AM";minLines=4;gravity=Gravity.TOP;inputType=InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE}
        l.addView(input);l.addView(MaterialButton(this).apply{text="GENERATE NOTICE";isAllCaps=false;setOnClickListener{makeNotice(input.text.toString(),l)}})
        c.addView(l);s.addView(c);content.removeAllViews();content.addView(p.first)
    }

    private fun makeNotice(topic:String,l:LinearLayout){
        if(topic.isBlank()){Toast.makeText(this,"Enter a notice topic.",Toast.LENGTH_SHORT).show();return}
        val e="NOTICE\n\nSubject: "+topic.trim()+"\n\nDear Students/Parents,\n\nThis is to inform you that "+topic.trim()+". All concerned students and parents are requested to take note and follow the instructions accordingly.\n\nThank you.\n\nClass Teacher\n"+school()
        val h="सूचना\n\nविषय: "+topic.trim()+"\n\nप्रिय विद्यार्थियों/अभिभावकों,\n\nआप सभी को सूचित किया जाता है कि "+topic.trim()+"। सभी संबंधित विद्यार्थियों एवं अभिभावकों से अनुरोध है कि इस सूचना को ध्यान में रखें और आवश्यक निर्देशों का पालन करें।\n\nधन्यवाद।\n\nकक्षा शिक्षक\n"+school()
        l.addView(TextView(this).apply{text="\nENGLISH\n\n"+e+"\n\n────────────\n\nHINDI\n\n"+h;textSize=15f;setTextIsSelectable(true);setPadding(0,12,0,12)})
        l.addView(MaterialButton(this).apply{text="SEND NOTICE → WhatsApp";isAllCaps=false;setOnClickListener{whatsapp(school()+"\n\n"+e+"\n\n"+h)}})
    }

    private fun settings(){
        val p=scroll();val s=p.second;val c=card();val l=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(18,18,18,18)}
        l.addView(TextView(this).apply{text="⚙ Settings";textSize=20f;setTypeface(typeface,1)})
        val school=EditText(this).apply{hint="School name";setText(school())};l.addView(school)
        l.addView(MaterialButton(this).apply{text="SAVE SCHOOL NAME";isAllCaps=false;setOnClickListener{prefs.edit().putString("school",school.text.toString().trim().ifBlank{"YOUR SCHOOL NAME"}).apply();Toast.makeText(this@MainActivity,"Saved",Toast.LENGTH_SHORT).show()}})
        l.addView(MaterialButton(this).apply{text="MANAGE CLASSES & STUDENTS";isAllCaps=false;setOnClickListener{manageClasses()}})
        l.addView(TextView(this).apply{text="\nWhatsApp\n\nThe app prepares the message and opens WhatsApp. You select the class group and tap Send.";textSize=13f;setTextColor(0xff687386.toInt())})
        c.addView(l);s.addView(c);content.removeAllViews();content.addView(p.first)
    }

    private fun manageClasses(){
        val n=classes();MaterialAlertDialogBuilder(this).setTitle("Classes").setItems(n.toTypedArray()){_,i->manageStudents(n[i])}.setPositiveButton("Add class"){_,_->addClass()}.setNegativeButton("Close",null).show()
    }
    private fun addClass(){
        val e=EditText(this).apply{hint="Example: 8A"}
        MaterialAlertDialogBuilder(this).setTitle("Add class").setView(e).setPositiveButton("Add"){_,_->val n=e.text.toString().trim();if(n.isNotBlank()){val a=JSONArray();(classes()+n).distinct().forEach{a.put(it)};prefs.edit().putString("classes",a.toString()).apply();saveStudents(n,emptyList());settings()}}.setNegativeButton("Cancel",null).show()
    }
    private fun manageStudents(c:String){
        val st=students(c);val lines=st.map{"Roll "+it.roll+" — "+it.name}.toTypedArray()
        MaterialAlertDialogBuilder(this).setTitle(c+" students").setItems(lines){_,_->}.setPositiveButton("Add student"){_,_->addStudent(c)}.setNegativeButton("Close",null).show()
    }
    private fun addStudent(c:String){
        val box=LinearLayout(this).apply{orientation=LinearLayout.VERTICAL;setPadding(20,0,20,0)}
        val r=EditText(this).apply{hint="Roll number";inputType=2};val n=EditText(this).apply{hint="Student name"};box.addView(r);box.addView(n)
        MaterialAlertDialogBuilder(this).setTitle("Add student").setView(box).setPositiveButton("Save"){_,_->val roll=r.text.toString().toIntOrNull();val name=n.text.toString().trim();if(roll!=null&&name.isNotBlank())saveStudents(c,students(c).filterNot{it.roll==roll}+Student(roll,name));settings()}.setNegativeButton("Cancel",null).show()
    }
    private fun chooseClass(){val n=classes();MaterialAlertDialogBuilder(this).setTitle("Select class").setItems(n.toTypedArray()){_,i->selectedClass=n[i];attendance()}.show()}
    private fun whatsapp(message:String){
        val i=Intent(Intent.ACTION_SEND).apply{type="text/plain";putExtra(Intent.EXTRA_TEXT,message);setPackage("com.whatsapp")}
        try{startActivity(i)}catch(_:Exception){startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply{type="text/plain";putExtra(Intent.EXTRA_TEXT,message)},"Send message"))}
    }
    private fun date()=SimpleDateFormat("dd/MM/yyyy",Locale.getDefault()).format(Date())
}