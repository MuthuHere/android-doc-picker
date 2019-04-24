package com.greentoad.turtlebody.docpicker.ui.common.doc_filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.greentoad.turtlebody.docpicker.R
import com.greentoad.turtlebody.docpicker.core.DocPickerConfig
import kotlinx.android.synthetic.main.tb_doc_picker_bottom_sheet_doc_filter_fragment.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class DocFilterFragment : BottomSheetDialogFragment(), AnkoLogger, DocFilterAdapter.OnDocFilterClickListener {

    companion object {
        @JvmStatic
        fun newInstance(key: Int, b: Bundle?): BottomSheetDialogFragment {
            var bf: Bundle = b ?: Bundle()
            bf.putInt("fragment.key", key);
            val fragment = DocFilterFragment()
            fragment.arguments = bf
            return fragment
        }
    }

    private var mDocFilterList: ArrayList<DocFilterModel> = arrayListOf()
    private var mAdapter: DocFilterAdapter = DocFilterAdapter()
    private var mPickerConfig: DocPickerConfig = DocPickerConfig()
    private var mOnFilterDoneListener: OnFilterDoneListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.tb_doc_picker_bottom_sheet_doc_filter_fragment, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if(arguments!=null){
            mPickerConfig = arguments?.getSerializable(DocPickerConfig.ARG_BUNDLE) as DocPickerConfig
        }

        for(i in mPickerConfig.mDocTypes){
            mDocFilterList.add((DocFilterModel(i)))
        }
        initAdapter()
        initButton()
    }

    private fun initButton() {
        tb_doc_picker_btn_done.setOnClickListener {
            info { "clicked" }

            val docTypes = arrayListOf<String>()
            for(i in mDocFilterList){
                if(i.isSelected){
                    docTypes.add(i.docType)
                }
            }
            if(docTypes.isNotEmpty()){
                mPickerConfig.mUserSelectedDocTypes = docTypes
                mOnFilterDoneListener?.onFilterDone(docTypes)
            }
            else
                Toast.makeText(context,"Please select at least one doc type.",Toast.LENGTH_LONG).show()

            tb_doc_picker_bottom_sheet_fragment_btn_cancel.setOnClickListener {
                this.dismiss()
            }
        }
    }

    override fun onDocCheck(pData: DocFilterModel) {
        val selectedIndex = mDocFilterList.indexOf(pData)

        if(selectedIndex >= 0){
            //toggle
            mDocFilterList[selectedIndex].isSelected = !(mDocFilterList[selectedIndex].isSelected)
            //update ui
            mAdapter.updateIsSelected(mDocFilterList[selectedIndex])
        }
    }

    private fun initAdapter() {
        mAdapter.setListener(this)
        tb_doc_picker_bottom_sheet_fragment_recycler_view.layoutManager = LinearLayoutManager(context)
        tb_doc_picker_bottom_sheet_fragment_recycler_view.adapter = mAdapter

        for(i in mDocFilterList){
            i.isSelected = mPickerConfig.mUserSelectedDocTypes.contains(i.docType)
        }

        mAdapter.setData(mDocFilterList)
    }


    override fun getTheme(): Int {
        return R.style.App_Dialog_BottomSheet
    }

    fun setListener(pListener: OnFilterDoneListener?) {
        mOnFilterDoneListener = pListener
    }


    interface OnFilterDoneListener {
        fun onFilterDone(list: ArrayList<String>)
    }
}