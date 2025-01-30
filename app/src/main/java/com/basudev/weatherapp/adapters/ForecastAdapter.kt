package com.basudev.weatherapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.basudev.weatherapp.R
import com.basudev.weatherapp.data.entity.Forecast
import java.text.SimpleDateFormat
import java.util.Locale

class ForecastAdapter : ListAdapter<Forecast, ForecastViewHolder>(ForecastDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_forecast, parent, false)
        return ForecastViewHolder(view)
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class ForecastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
    private val tvTemp: TextView = itemView.findViewById(R.id.tvTemp)
    private val tvCondition: TextView = itemView.findViewById(R.id.tvCondition)
    private val ivIcon: ImageView = itemView.findViewById(R.id.ivIcon)

    fun bind(forecast: Forecast) {
        // Date formatting
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = inputFormat.parse(forecast.date)
        val displayFormat = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
        tvDate.text = date?.let { displayFormat.format(it) } ?: forecast.date

        // Temperature formatting
        tvTemp.text = itemView.context.getString(
            R.string.min_max_temp_forecast_format,
            forecast.maxTemp,
            forecast.minTemp
        )

        // Weather condition
        tvCondition.text = forecast.condition

        // Weather icon with Glide
        Glide.with(itemView)
            .load("https://openweathermap.org/img/wn/${forecast.icon}@2x.png")
            .placeholder(R.drawable.ic_weather_loading)
            .error(R.drawable.ic_weather_error)
            .into(ivIcon)
    }
}

class ForecastDiffCallback : DiffUtil.ItemCallback<Forecast>() {
    override fun areItemsTheSame(oldItem: Forecast, newItem: Forecast): Boolean {
        return oldItem.date == newItem.date
    }

    override fun areContentsTheSame(oldItem: Forecast, newItem: Forecast): Boolean {
        return oldItem == newItem
    }
}