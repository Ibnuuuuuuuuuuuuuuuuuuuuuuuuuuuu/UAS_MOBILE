package com.example.kopipedia.presentation.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.kopipedia.databinding.FragmentCoffeeIdentifierBinding
import kotlinx.coroutines.*
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.core.BaseOptions
import org.tensorflow.lite.task.vision.classifier.ImageClassifier
import java.io.IOException

class CoffeeIdentifierFragment : Fragment() {

    private var _binding: FragmentCoffeeIdentifierBinding? = null
    private val binding get() = _binding!!

    // Variabel untuk menampung ImageClassifier
    private var imageClassifier: ImageClassifier? = null
    // Coroutine scope untuk menjalankan tugas di background
    private val classificationScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    // Launcher modern untuk memilih gambar dari galeri
    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            data?.data?.let { uri ->
                // Konversi URI ke Bitmap dan mulai klasifikasi
                val bitmap = uriToBitmap(uri)
                if (bitmap != null) {
                    binding.ivPreviewImage.setImageBitmap(bitmap)
                    classifyImage(bitmap)
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCoffeeIdentifierBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup classifier saat view dibuat
        setupClassifier()

        binding.btnPilihGambar.setOnClickListener {
            openGallery()
        }
    }

    // Fungsi untuk menginisialisasi ImageClassifier
    private fun setupClassifier() {
        val options = ImageClassifier.ImageClassifierOptions.builder()
            .setBaseOptions(BaseOptions.builder().setNumThreads(2).build())
            .setMaxResults(1) // Kita hanya butuh 1 hasil teratas
            .build()
        try {
            // Membuat instance ImageClassifier dari model di folder assets
            imageClassifier = ImageClassifier.createFromFileAndOptions(
                requireContext(),
                "model_kopi.tflite", // Nama model yang sudah kita siapkan
                options
            )
        } catch (e: IOException) {
            Log.e("Classifier", "Error initializing ImageClassifier", e)
            Toast.makeText(context, "Gagal memuat model klasifikasi. Pastikan file ada di assets.", Toast.LENGTH_LONG).show()
        }
    }

    // Fungsi untuk membuka galeri
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    // Fungsi utama untuk menjalankan klasifikasi gambar
    private fun classifyImage(bitmap: Bitmap) {
        if (imageClassifier == null) {
            Toast.makeText(context, "Classifier belum siap.", Toast.LENGTH_SHORT).show()
            return
        }

        // Tampilkan loading
        showLoading(true)

        // Jalankan klasifikasi di background thread menggunakan coroutine
        classificationScope.launch {
            try {
                // Konversi Bitmap ke format yang dimengerti TensorFlow
                val tensorImage = TensorImage.fromBitmap(bitmap)
                // Jalankan proses klasifikasi
                val results = imageClassifier?.classify(tensorImage)

                // Pindah ke UI thread untuk menampilkan hasil
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    if (!results.isNullOrEmpty() && !results[0].categories.isNullOrEmpty()) {
                        val category = results[0].categories[0]
                        val resultText = "Prediksi: ${category.label.replaceFirstChar { it.uppercase() }}\nKeyakinan: ${"%.1f".format(category.score * 100)}%"
                        binding.tvHasilPrediksi.text = resultText
                    } else {
                        binding.tvHasilPrediksi.text = "Tidak dapat mengenali gambar."
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    Log.e("Classifier", "Classification failed", e)
                    binding.tvHasilPrediksi.text = "Gagal melakukan klasifikasi."
                }
            }
        }
    }

    // Helper function untuk mengubah URI ke Bitmap
    private fun uriToBitmap(uri: Uri): Bitmap? {
        return try {
            MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBarKlasifikasi.isVisible = isLoading
        binding.btnPilihGambar.isEnabled = !isLoading
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Batalkan semua coroutine yang berjalan untuk menghindari memory leak
        classificationScope.cancel()
        _binding = null
    }
}