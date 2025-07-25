package com.example.lotteryprediction.deepseek

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lotteryprediction.databinding.ActivityDeepseekChatBinding
import com.example.lotteryprediction.databinding.ItemChatMessageBinding
import com.example.lotteryprediction.deepseek.model.ChatMessage
import com.example.lotteryprediction.R
import kotlinx.coroutines.launch

class DeepSeekChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDeepseekChatBinding
    private lateinit var adapter: ChatAdapter
    private val chatMessages = mutableListOf<ChatMessage>()
    private lateinit var deepSeekService: DeepSeekService
    private lateinit var deepSeekClient: DeepSeekClient
    private lateinit var versionHistoryManager: VersionHistoryManager
    private var menu: Menu? = null
    private val coroutineJobs = mutableListOf<Job>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeepseekChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupClickListeners()
        
        // 初始化DeepSeek服务
        val config = DeepSeekConfig(this)  // 使用Activity context
        deepSeekService = DeepSeekService(config)
        deepSeekClient = DeepSeekClient(config)
        deepSeekService.initKnowledgeBase(this)
        versionHistoryManager = VersionHistoryManager(this)
        
        // 初始化版本历�?        coroutineJobs += lifecycleScope.launch {
            versionHistoryManager.addVersion(
                code = 1,
                name = "1.0.0",
                date = "2023-11-20",
                changes = "初始版本\n- 基础彩票查询功能\n- DeepSeek集成"
            )
        }
    }

    private fun showVersionHistory() {
        coroutineJobs += lifecycleScope.launch {
            try {
                val history = versionHistoryManager.getHistory()
                val message = buildString {
                    append("版本更新历史:\n\n")
                    history.forEach {
                        append("${it.versionName} (${it.releaseDate}):\n")
                        append("${it.changes}\n\n")
                    }
                }
                addMessage(message, isUser = false)
            } catch (e: Exception) {
                addMessage("获取版本历史失败: ${e.message}", isUser = false)
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = ChatAdapter(chatMessages)
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.chatRecyclerView.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.deepseek_chat_menu, menu)
        this.menu = menu
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menuVersionHistory -> {
                showVersionHistory()
                true
            }
            R.id.menuFeedback -> {
                showFeedbackDialog()
                true
            }
            R.id.menuAnalysis -> {
                analyzeLotteryData()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupClickListeners() {
        binding.sendButton?.setOnClickListener {
            sendMessage()
        }

        binding.optimizeButton?.setOnClickListener {
            optimizePrediction()
        }

        binding.knowledgeButton?.setOnClickListener {
            queryKnowledgeBase()
        }
    }

    private fun showFeedbackDialog() {
        FeedbackDialog().show(supportFragmentManager, "feedback_dialog")
    }

    private fun sendMessage() {
        val message = binding.messageEditText?.text?.toString() ?: ""
        if (message.isNotEmpty()) {
            PerformanceMonitor.startTrace("chat_request")
            PerformanceMonitor.logMemoryUsage()
            
            addMessage(message, isUser = true)
            binding.messageEditText?.text?.clear()
            binding.sendButton?.isEnabled = false
            
            coroutineJobs += lifecycleScope.launch {
                try {
                    val response = deepSeekClient.chat(message)
                    addMessage(response, isUser = false)
                } catch (e: Exception) {
                    addMessage("请求失败: ${e.message}", isUser = false)
                } finally {
                    PerformanceMonitor.endTrace("chat_request")
                    PerformanceMonitor.logMemoryUsage()
                    binding.sendButton?.isEnabled = true
                }
            }
        }
    }

    private fun optimizePrediction() {
        addMessage("正在优化预测算法...", isUser = false)
        binding.optimizeButton?.isEnabled = false
        
        coroutineJobs += lifecycleScope.launch {
            try {
                val (algorithm, params) = deepSeekClient.optimizePrediction(
                    algorithm = "lottery_prediction_v2",
                    parameters = mapOf("history_size" to 100, "window_size" to 5),
                    hardwareConstraints = mapOf("memory" to 512, "cpu" to 1)
                )
                addMessage("优化完成！新算法: $algorithm\n参数: $params", isUser = false)
            } catch (e: Exception) {
                addMessage("优化失败: ${e.message}", isUser = false)
            } finally {
                binding.optimizeButton?.isEnabled = true
            }
        }
    }

    private fun queryKnowledgeBase() {
        binding.knowledgeButton?.isEnabled = false
        addMessage("正在查询知识�?..", isUser = false)
        
        coroutineJobs += lifecycleScope.launch {
            try {
                val historyData = LotteryKnowledgeBase.getHistoryData()
                val message = buildString {
                    append("最新开奖数�?\n")
                    historyData.take(5).forEach { 
                        append("${it.period}�? ${it.numbers.joinToString()} (${it.date})\n")
                    }
                    append("\n输入具体期号查询详情")
                }
                addMessage(message, isUser = false)
            } catch (e: Exception) {
                addMessage("查询失败: ${e.message}", isUser = false)
            } finally {
                binding.knowledgeButton?.isEnabled = true
            }
        }
    }

    private fun analyzeLotteryData() {
        // 使用缓存的menu变量
        val menuItem = menu?.findItem(R.id.menuAnalysis)
        menuItem?.isEnabled = false
        addMessage("正在分析彩票数据...", isUser = false)
        
        coroutineJobs += lifecycleScope.launch {
            try {
                val response = deepSeekClient.analyzeData(
                    dataType = "lottery",
                    timeRange = Pair("2023-01-01", "2023-12-31"),
                    metrics = listOf("frequency", "hot_numbers", "cold_numbers")
                )
                
                val message = buildString {
                    append("数据分析结果:\n\n")
                    append("${response.summary}\n\n")
                    append("关键发现:\n")
                    response.insights.forEach { append("- $it\n") }
                }
                addMessage(message, isUser = false)
            } catch (e: Exception) {
                addMessage("分析失败: ${e.message}", isUser = false)
            } finally {
                menu?.findItem(R.id.menuAnalysis)?.isEnabled = true
            }
        }
    }

    private fun addMessage(text: String, isUser: Boolean) {
        val message = ChatMessage(text, isUser)
        chatMessages.add(message)
        adapter.notifyItemInserted(chatMessages.size - 1)
        binding.chatRecyclerView.smoothScrollToPosition(chatMessages.size - 1)
    }
}

    override fun onDestroy() {
        super.onDestroy()
        // 取消所有未完成的协程任�?        coroutineJobs.forEach { it.cancel() }
        // 清理资源
        binding.chatRecyclerView.adapter = null
        menu = null
    }
}
class ChatAdapter(private val messages: List<ChatMessage>) : 
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    inner class ChatViewHolder(val binding: ItemChatMessageBinding) : 
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = ItemChatMessageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = messages[position]
        holder.binding.messageText.text = message.text
        holder.binding.messageCard.setCardBackgroundColor(
            if (message.isUser) Color.parseColor("#E3F2FD") 
            else Color.parseColor("#FFFFFF")
        )
    }

    override fun getItemCount() = messages.size
}
