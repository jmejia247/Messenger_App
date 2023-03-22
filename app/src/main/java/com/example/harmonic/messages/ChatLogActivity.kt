package com.example.harmonic.messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.harmonic.NewMessageActivity
import com.example.harmonic.R
import com.example.harmonic.models.ChatMessages
import com.example.harmonic.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*
import java.sql.Timestamp

class ChatLogActivity : AppCompatActivity() {

    val adapter = GroupieAdapter()
    var toUser : User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)
        recyclerview_chat_log.adapter = adapter

        toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)!!
        supportActionBar?.title = toUser?.email

        listenForMessages()

        send_button_chat_log.setOnClickListener {
            Log.d("Chatlog", "send message functionality")
            sendMessage()
        }
    }

    private fun listenForMessages() {
//        val reference = FirebaseDatabase.getInstance().getReference("/messages")

        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid

        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

        reference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessages::class.java)

                if (chatMessage != null) {
                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                        Log.d("Chat", "this is the from")
                        val currentUser = HomeActivity.currentUser ?: return
                        adapter.add(ChatToItem(chatMessage.text, currentUser))
                    } else {
                        adapter.add(ChatFromItem(chatMessage.text, toUser!!))
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }
        })
    }


    private fun sendMessage() {
//        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val fromId = FirebaseAuth.getInstance()
        val toId = toUser?.uid
        val messages = FirebaseDatabase.getInstance().getReference("/user-messages/${fromId.uid}/$toId").push()

        val toMessage = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/${fromId.uid}").push()

        val message = message_chat_log.text.toString()
        val text = ChatMessages(
            messages.key!!,
            message,
            fromId.uid!!,
            toId!!,
            System.currentTimeMillis() / 1000
        )
        messages.setValue(text).addOnSuccessListener {
            Log.d("Chat", "Messages successfully sent to: ${toUser?.email} from: ${fromId.currentUser?.email}")
            message_chat_log.text.clear()
            recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
        }
        toMessage.setValue(text)
    }
}

class ChatFromItem(val text: String, val user: User): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.from_text.text = text

        val uri = user.profile_img
        Picasso.get().load(uri).into(viewHolder.itemView.from_imageView)
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}

class ChatToItem(val text: String, val user: User): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.to_text.text = text

        val uri = user.profile_img
        Picasso.get().load(uri).into(viewHolder.itemView.to_imageView)
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}













