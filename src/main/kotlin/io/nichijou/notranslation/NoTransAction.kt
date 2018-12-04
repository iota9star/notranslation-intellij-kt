package io.nichijou.notranslation

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.ui.popup.Balloon
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.ui.JBColor
import com.intellij.ui.awt.RelativePoint
import java.awt.Color

class NoTransAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(PlatformDataKeys.EDITOR) ?: return
        val location: RelativePoint = JBPopupFactory.getInstance().guessBestPopupLocation(editor)
        val txt = editor.selectionModel.selectedText
        if (txt.isNullOrBlank()) return
        Net.translate(txt, {
            if (it.data.isEmpty()) {
                show(location, "没有匹配的建议")
            } else {
                val sb = StringBuilder()
                it.data.forEachIndexed { index, data ->
                    if (index == it.data.size - 1) {
                        sb.append(data.k).append(" ").append(data.v)
                    } else {
                        sb.append(data.k).append(" ").append(data.v).append("\n")
                    }
                }
                show(location, sb.toString())
            }
        }, {
            show(location, it ?: return@translate)
        })
    }

    private fun show(location: RelativePoint, str: String) {
        ApplicationManager.getApplication().invokeLater {
            JBPopupFactory.getInstance()
                .createHtmlTextBalloonBuilder(
                    str,
                    null,
                    JBColor(Color.LIGHT_GRAY, Color.DARK_GRAY),
                    null
                )
                .setFadeoutTime(15000)
                .setBorderColor(Color.LIGHT_GRAY)
                .setHideOnAction(true)
                .createBalloon()
                .show(location, Balloon.Position.below)
        }
    }
}

