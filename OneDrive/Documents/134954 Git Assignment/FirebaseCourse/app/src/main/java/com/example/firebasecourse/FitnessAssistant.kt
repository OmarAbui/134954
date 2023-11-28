package com.example.firebasecourse

import android.os.Bundle
import android.os.Message
import android.speech.tts.TextToSpeech
import android.view.Menu
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.RetryPolicy
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.firebasecourse.databinding.ActivityFitnessAssistantBinding
import com.stfalcon.chatkit.commons.ImageLoader
import com.stfalcon.chatkit.messages.MessagesList
import com.stfalcon.chatkit.messages.MessagesListAdapter
import org.json.JSONObject
import java.util.Calendar
import java.util.Locale


class FitnessAssistant : AppCompatActivity() {
    //initialising three variables
    lateinit var sendButton:ImageButton
    lateinit var queryView:EditText
    lateinit var fitnessAssistantBinding: ActivityFitnessAssistantBinding
    lateinit var messagesList:MessagesList
    lateinit var us:UserChat
    lateinit var fitBot:UserChat
    lateinit var adapter:MessagesListAdapter<Messages>
    lateinit var tts:TextToSpeech

    //Below is an API key for chatGPT
    //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

    //@SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fitnessAssistantBinding= ActivityFitnessAssistantBinding.inflate(layoutInflater)
        val view=fitnessAssistantBinding.root
        setContentView(view)


        //referencing the variables with their ID's
        sendButton=findViewById(R.id.imageButton)
        queryView=findViewById(R.id.editTextText)
        messagesList=findViewById(R.id.messagesList)

        val imageLoader:ImageLoader=object:ImageLoader{
            override fun loadImage(imageView: ImageView?, url: String?, payload: Any?) {

            }

        }
         adapter = MessagesListAdapter<Messages>("1", imageLoader)
        messagesList.setAdapter(adapter)


        us= UserChat("1","me", "")
        fitBot=UserChat("2","FitnessBot","")

        val welcomeMessage = "Hello Tough Guy, My names are Omar Mahmoud, Did you perform a running workout today?\n" +
                "If so, how did you feel? Was it hard?, How many kilometers did you manage to cover today? Tell me about the workout\n" +
                "and i will try my best to give you advice in terms of what to improve in your next running workout. Also, remember,\n" +
                "we can also chat about other fitness topics for example your nutrition, what foods will give you a lean body\n" +
                "and other fitness topics. Feel free to ask me anything related to fitness!!"

        sendBotMessage(welcomeMessage)


        //When clicked, it will call the performAction function
        sendButton.setOnClickListener{
            var message:Messages=Messages("m1",queryView.text.toString(),us,Calendar.getInstance().time)
            adapter.addToStart(message,true)
            performAction(queryView.text.toString()) //calling the function
            queryView.text.clear()
        }

        tts=TextToSpeech(applicationContext, TextToSpeech.OnInitListener {task->
            if(task!=TextToSpeech.ERROR){
                tts.setLanguage(Locale.ENGLISH)
            }
        })

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return true
    }


    //This function will be responsible for sending queries to the chatgpt model after the sendButton is clicked.
    fun performAction(input:String) {

        //Volley Library code for using the URL
        val queue = Volley.newRequestQueue(this)

        //ChatGPT URL
        val url = "https://api.openai.com/v1/completions"

        //creating a JSON Object
        val jObject= JSONObject()
        jObject.put("model","gpt-3.5-turbo-instruct-0914")
       // val jArray:JSONArray= JSONArray("[{\"role\": \"user\", \"content\": \""+input+"\"}]")
        jObject.put("prompt", input)
        jObject.put("max_tokens", 250)
        jObject.put("temperature",0)

        // Request a string response from the provided URL.
        val stringRequest = object:JsonObjectRequest(Request.Method.POST, url,jObject,
            Response.Listener<JSONObject> { response ->
                // Display the first 500 characters of the response string.
                //Below is turbo model chat
                //var answer= response.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content")
                //Below is for legacy turbo-instruct
                val answer= response.getJSONArray("choices").getJSONObject(0).getString("text")
                //responseView.text = answer
                var message:Messages=Messages("m2",answer.trim(),fitBot,Calendar.getInstance().time)
                adapter.addToStart(message,true)
                tts.speak(answer,TextToSpeech.QUEUE_FLUSH,null,null)
            },
            Response.ErrorListener { })
        {
            override fun getHeaders(): MutableMap<String, String> {
                var map=HashMap<String,String>()

                //Content format
                map.put("Content-Type", "application/json")
                map.put("Authorization", "Bearer API_KEY")
                return map
            }
        }
        stringRequest.retryPolicy = object:RetryPolicy{
            override fun getCurrentTimeout(): Int {
                return 7500;
            }

            override fun getCurrentRetryCount(): Int {
                return 10;
            }

            override fun retry(error: VolleyError?) {

            }

        }

// Add the request to the RequestQueue.
        queue.add(stringRequest)
    }

    fun sendBotMessage(message: String) {
        val botUser = UserChat("bot_id", "FitnessBot", "bot_avatar")
        val botMessage = Messages("bot_message_id", message, botUser, Calendar.getInstance().time)
        adapter.addToStart(botMessage, true)
    }

}