package ss.anoop.awesomeloaders.demo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.loader_list_item.view.*

class AwesomeLoaderListAdapter(private val loaders: List<AwesomeLoaderData>) :
    RecyclerView.Adapter<AwesomeLoaderListAdapter.AwesomeLoaderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        AwesomeLoaderViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.loader_list_item, parent, false)
        )

    override fun getItemCount() = loaders.size

    override fun onBindViewHolder(holder: AwesomeLoaderViewHolder, position: Int) {
        holder.bindView(loaders[position])
    }

    inner class AwesomeLoaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindView(data: AwesomeLoaderData) {
            with(itemView) {
                loaderItemContainer.run {
                    removeAllViews()
                    addView(data.view)
                }
                loaderItemTitle.text = data.title
            }
        }
    }
}