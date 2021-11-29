/*
 *     Copyright (c) 2021 Zachary. All rights reserved.
 *
 *     This file is part of Codroid.
 *
 *     Codroid is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Codroid is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Codroid.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.codroid.editor.ui.projectstruct

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseNodeAdapter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.entity.node.BaseNode
import com.chad.library.adapter.base.provider.BaseNodeProvider
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import kotlinx.coroutines.*
import org.codroid.editor.R
import org.codroid.editor.databinding.ItemProjectStructBinding
import org.codroid.editor.ui.FileItem
import org.codroid.editor.widgets.ProjectStructureItemView
import org.codroid.interfaces.addon.AddonManager
import org.codroid.interfaces.evnet.EventCenter
import org.codroid.interfaces.evnet.editor.ProjectStructItemLoadEvent
import org.codroid.interfaces.evnet.entities.ProjectStructItemEntity
import java.io.File
import java.lang.Exception

class ProjectStructureAdapter() :
    BaseQuickAdapter<FileTreeNode, BaseDataBindingHolder<ItemProjectStructBinding>>(R.layout.item_project_struct) {

    private val scope = CoroutineScope(Dispatchers.Main)

    override fun convert(
        holder: BaseDataBindingHolder<ItemProjectStructBinding>,
        item: FileTreeNode
    ) {
        var addon: ProjectStructItemEntity? = null

        val itemView = holder.dataBinding?.projectStructureItem

        var type = ProjectStructureItemView.FILE

        if (item.element?.isDirectory == true) {
            type = ProjectStructureItemView.DIRECTORY
        }

        holder.dataBinding?.item =
            item.element?.name?.let {
                FileItem(it, null, Color.BLACK, type, item.isExpanded, item.level)
            }

        scope.launch {
            withContext(Dispatchers.Default) {
                AddonManager.get().eventCenter()
                    .executeStream<ProjectStructItemLoadEvent>(EventCenter.EventsEnum.PROJECT_STRUCT_ITEM_LOAD)
                    .forEach {
                        try {
                            it?.beforeLoading(item.element)?.let { entity ->
                                addon = entity
                            }
                        } catch (e: Exception) {
                            AddonManager.get().logger.e("An event: ${it.javaClass.name} execute failed! ( ${e.message} )")
                            e.printStackTrace()
                        }
                    }

                if (addon?.icon != null) {
                    addon?.icon?.let {
                        itemView?.setImageBitmap(it.toBitmap())
                    }
                } else {
                    itemView?.setImageBitmap(initIcon(type))
                }

                addon?.let { it ->

                    if (addon?.tagIcon != null) {
                        itemView?.setTagBitmap(addon?.tagIcon?.toBitmap())
                    } else {
                        itemView?.setTagBitmap(null)
                    }

                    it.title?.let { str ->
                        itemView?.setTitle(str)
                    }
                }
            }
        }
    }

    private fun initIcon(type: String): Bitmap {
        // Init the icon bitmap
        return when (type) {
            ProjectStructureItemView.DIRECTORY -> ResourcesCompat.getDrawable(
                context.resources,
                R.drawable.ic_twotone_folder_open_24,
                context.theme
            )?.toBitmap()!!
            else -> {
                ResourcesCompat.getDrawable(
                    context.resources,
                    R.drawable.ic_twotone_description_24,
                    context.theme
                )?.toBitmap()!!
            }
        }
    }


    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        scope.cancel()
    }

    fun close(position: Int) {
        var now = position + 1
        val parentNode = getItem(position)
        while (now > 0) {
            if (getItem(now).level > parentNode.level) {
                removeAt(now)
            } else {
                break
            }
        }
        parentNode.isExpanded = false
    }

    fun expand(position: Int, leaf: List<FileTreeNode>) {
        addData(position + 1, leaf)
        getItem(position).isExpanded = true
    }
}