package com.example.myapplication.deepseek.task

import android.content.Context
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.text.TextPaint
import android.text.style.TextAppearanceSpan
import com.example.myapplication.deepseek.util.LogUtils
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date
import java.util.LinkedHashMap
import java.util.Locale
import kotlin.String
import kotlin.Unit
import kotlin.Float
import kotlin.collections.split
import kotlin.collections.joinToString
import kotlin.collections.forEach
import java.util.Locale

/**
 * 报告导出为PDF功能
 */
object ReportExporter {
    private const val TAG = "ReportExporter"
    private val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())

    fun exportToPdf(context: Context, content: String): File? {
        return try {
            // 直接使用context，Kotlin参数默认非空
            context.getExternalFilesDir(null) // 示例实际使用
            // 创建PDF文档
            val document = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4尺寸
            val page = document.startPage(pageInfo)
            
            // 绘制文本到PDF
            val paint = TextPaint()
            var canvas = page.canvas
            val styledText = content.split("\n").joinToString("\n") { line ->
                if (line.startsWith("Feature") || line.contains(":")) {
                    // 加粗标题行
                    "**$line**"
                } else {
                    line
                }
            }
            
            // 简单布局 - 实际项目应使用更专业的PDF生成库
            var yPos = 50f
            styledText.split("\n").forEach { line ->
                canvas.drawText(line, 50f, yPos, paint)
                yPos += 30f
                if (yPos > 800f) {
                    document.finishPage(page)
                    val newPage = document.startPage(pageInfo)
                    canvas = newPage.canvas
                    yPos = 50f
                }
            }
            
            document.finishPage(page)
            
            // 保存文件
            val exportDir: File = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                "FeatureReports"
            ).apply { mkdirs() }
            
            val pdfFile = File(exportDir, "feature_report_${dateFormat.format(Date())}.pdf")
            FileOutputStream(pdfFile).use { output: FileOutputStream ->
                document.writeTo(output)
            }
            
            document.close()
            LogUtils.i(TAG, "PDF exported to ${pdfFile.absolutePath}")
            pdfFile
        } catch (e: Exception) {
            LogUtils.e(TAG, "Failed to export PDF", e)
            null
        }
    }
}
