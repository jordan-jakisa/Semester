package com.bawano.semester.ui.pdf

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bawano.semester.R
import com.bawano.semester.databinding.RvPdfItemBinding
import com.bawano.semester.databinding.RvPdfItemHorizontalBinding
import com.bawano.semester.utils.Utils
import com.bawano.semester.utils.getPdfImageFile
import com.bumptech.glide.Glide


class PdfAdapter(fragment: Fragment) : ListAdapter<String, PdfAdapter.PdfViewHolder?>(callback) {
    private val onPdf: Utils.OnPdf
    private var isHorizontal = false

    fun useHorizontalLayout(horizontal: Boolean) {
        isHorizontal = horizontal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PdfViewHolder.from(parent, onPdf, isHorizontal)

    override fun onBindViewHolder(holder: PdfViewHolder, position: Int) =
        holder.bind(getItem(position))


    class PdfViewHolder private constructor(
        bh: RvPdfItemHorizontalBinding,
        b: RvPdfItemBinding,
        onPdf: Utils.OnPdf,
        isHorizontal: Boolean
    ) :
        RecyclerView.ViewHolder(if (isHorizontal) bh.root else b.root) {
        private val bh: RvPdfItemHorizontalBinding
        private val b: RvPdfItemBinding
        private val onPdf: Utils.OnPdf
        private val isHorizontal: Boolean
        fun bind(item: String) {
            if (isHorizontal) {
                bh.name.text = item
                Glide.with(bh.pdfImage).load(itemView.context.getPdfImageFile(item))
                    .fallback(R.drawable.pdf).centerCrop().into(bh.pdfImage)
                bh.root.setOnClickListener { onPdf.click(item) }
            } else {
                b.name.text = item
                Glide.with(b.pdfImage).load(itemView.context.getPdfImageFile(item))
                    .fallback(R.drawable.pdf).centerCrop().into(b.pdfImage)
                b.root.setOnClickListener {  onPdf.click(item) }
            }
        }

        companion object {
            fun from(parent: ViewGroup, onPdf: Utils.OnPdf, isHorizontal: Boolean): PdfViewHolder {
                val bh: RvPdfItemHorizontalBinding = RvPdfItemHorizontalBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                val b: RvPdfItemBinding =
                    RvPdfItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return PdfViewHolder(bh, b, onPdf, isHorizontal)
            }
        }

        init {
            this.b = b
            this.bh = bh
            this.onPdf = onPdf
            this.isHorizontal = isHorizontal
        }
    }

    companion object {
        private val callback: DiffUtil.ItemCallback<String> =
            object : DiffUtil.ItemCallback<String>() {
                override fun areItemsTheSame(oldItem: String, newItem: String) = oldItem == newItem

                override fun areContentsTheSame(oldItem: String, newItem: String) =
                    oldItem == newItem
            }
    }

    init {
        onPdf = fragment as Utils.OnPdf
    }
}
