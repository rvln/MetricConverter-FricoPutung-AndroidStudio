package com.example.metricconverter

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var metricSpinner: Spinner
    private lateinit var unitFromSpinner: Spinner
    private lateinit var unitToSpinner: Spinner
    private lateinit var inputValue: EditText
    private lateinit var resultText: TextView
    private lateinit var convertButton: TextView

    private val metricOptions = arrayOf("--Pilih Metrik", "Panjang", "Massa", "Waktu", "Arus Listrik", "Suhu", "Intensitas Cahaya", "Jumlah Zat")
    private val unitsMap = mapOf(
        "Panjang" to arrayOf("Meter", "Centimeter", "Kilometer", "Milimeter"),
        "Massa" to arrayOf("Kilogram", "Gram"),
        "Waktu" to arrayOf("Detik", "Menit", "Jam"),
        "Arus Listrik" to arrayOf("Ampere", "Milliampere"),
        "Suhu" to arrayOf("Celsius", "Kelvin", "Fahrenheit"),
        "Intensitas Cahaya" to arrayOf("Candela"),
        "Jumlah Zat" to arrayOf("Mole", "Millimole")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        metricSpinner = findViewById(R.id.metricSpinner)
        unitFromSpinner = findViewById(R.id.unitFromSpinner)
        unitToSpinner = findViewById(R.id.unitToSpinner)
        inputValue = findViewById(R.id.inputValue)
        resultText = findViewById(R.id.resultText)
        convertButton = findViewById(R.id.btn_convert)

        // Initially disable dropdowns, input, and button
        unitFromSpinner.isEnabled = false
        unitToSpinner.isEnabled = false
        inputValue.isEnabled = false
        convertButton.isEnabled = false

        // Set up metric spinner with options
        val metricAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, metricOptions)
        metricAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        metricSpinner.adapter = metricAdapter

        // Set listener for metricSpinner
        metricSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                if (position != 0) {  // Position 0 is "Pilih Opsi"
                    val selectedMetric = metricOptions[position]
                    updateUnitSpinners(selectedMetric)
                } else {
                    // Reset and disable other spinners
                    unitFromSpinner.isEnabled = false
                    unitToSpinner.isEnabled = false
                    inputValue.isEnabled = false
                    convertButton.isEnabled = false
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Enable conversion when input field loses focus
        inputValue.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateInputAndConvert()
            }
        }

        convertButton.setOnClickListener {
            validateInputAndConvert()
        }
    }

    private fun updateUnitSpinners(metric: String) {
        val units = unitsMap[metric] ?: arrayOf()

        // Menambahkan item hint pada awal array satuan
        val unitsWithHint = arrayOf("--Pilih Satuan--") + units

        // Membuat adapter untuk unitFromSpinner dan unitToSpinner
        val unitAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, unitsWithHint)
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        unitFromSpinner.adapter = unitAdapter
        unitToSpinner.adapter = unitAdapter

        // Menandai bahwa kedua spinner aktif dan bisa dipilih
        unitFromSpinner.isEnabled = true
        unitToSpinner.isEnabled = true
        inputValue.isEnabled = true
        convertButton.isEnabled = true
    }


    private fun validateInputAndConvert() {
        val inputText = inputValue.text.toString()

        if (inputText.isEmpty() || inputText.toDoubleOrNull() == null) {
            Toast.makeText(this, "Masukkan nilai yang valid", Toast.LENGTH_SHORT).show()
            return
        }

        val input = inputText.toDouble()
        val fromUnit = unitFromSpinner.selectedItem?.toString() ?: ""
        val toUnit = unitToSpinner.selectedItem?.toString() ?: ""

        val result = convertUnits(input, fromUnit, toUnit)
        resultText.text = result.toString()
    }

    private fun convertUnits(input: Double, fromUnit: String, toUnit: String): Double {
        if (fromUnit == toUnit) return input // Jika satuan sama, tidak perlu konversi

        return when {
            // Konversi Panjang
            fromUnit == "Meter" && toUnit == "Kilometer" -> input / 1000
            fromUnit == "Meter" && toUnit == "Milimeter" -> input * 1000
            fromUnit == "Meter" && toUnit == "Centimeter" -> input * 100
            fromUnit == "Kilometer" && toUnit == "Meter" -> input * 1000
            fromUnit == "Milimeter" && toUnit == "Meter" -> input / 1000
            fromUnit == "Kilometer" && toUnit == "Centimeter" -> input * 100000
            fromUnit == "Centimeter" && toUnit == "Meter" -> input / 100
            fromUnit == "Centimeter" && toUnit == "Milimeter" -> input * 10
            fromUnit == "Kilometer" && toUnit == "Milimeter" -> input * 1000000
            fromUnit == "Milimeter" && toUnit == "Kilometer" -> input / 1000000
            fromUnit == "Milimeter" && toUnit == "Centimeter" -> input / 10


            // Konversi Massa
            fromUnit == "Kilogram" && toUnit == "Gram" -> input * 1000
            fromUnit == "Gram" && toUnit == "Kilogram" -> input / 1000

            // Konversi Waktu
            fromUnit == "Detik" && toUnit == "Menit" -> input / 60
            fromUnit == "Detik" && toUnit == "Jam" -> input / 3600
            fromUnit == "Menit" && toUnit == "Detik" -> input * 60
            fromUnit == "Menit" && toUnit == "Jam" -> input / 60
            fromUnit == "Jam" && toUnit == "Detik" -> input * 3600
            fromUnit == "Jam" && toUnit == "Menit" -> input * 60

            // Konversi Arus Listrik
            fromUnit == "Ampere" && toUnit == "Milliampere" -> input * 1000
            fromUnit == "Milliampere" && toUnit == "Ampere" -> input / 1000

            // Konversi Suhu
            fromUnit == "Celsius" && toUnit == "Kelvin" -> input + 273.15
            fromUnit == "Kelvin" && toUnit == "Celsius" -> input - 273.15
            fromUnit == "Celsius" && toUnit == "Fahrenheit" -> input * 9 / 5 + 32
            fromUnit == "Fahrenheit" && toUnit == "Celsius" -> (input - 32) * 5 / 9
            fromUnit == "Kelvin" && toUnit == "Fahrenheit" -> (input - 273.15) * 9 / 5 + 32
            fromUnit == "Fahrenheit" && toUnit == "Kelvin" -> (input - 32) * 5 / 9 + 273.15

            // Konversi Jumlah Zat
            fromUnit == "Mole" && toUnit == "Millimole" -> input * 1000
            fromUnit == "Millimole" && toUnit == "Mole" -> input / 1000

            else -> {
                Toast.makeText(this, "Konversi tidak tersedia", Toast.LENGTH_SHORT).show()
                0.0
            }
        }
    }
}

