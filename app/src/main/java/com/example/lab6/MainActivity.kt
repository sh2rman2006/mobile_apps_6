package com.example.lab6

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Timer
import java.util.TimerTask

class MainActivity : AppCompatActivity() {

    private lateinit var tvStatus: TextView
    private lateinit var btnStart: Button

    private var timer: Timer? = null
    private var currentNumber = 0
    private var currentSum = 0

    // Тег для фильтрации в Logcat (требование методички)
    private val TAG = "Lab6"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvStatus = findViewById(R.id.tvStatus)
        btnStart = findViewById(R.id.btnStart)

        btnStart.setOnClickListener {
            startCalculations()
        }
    }

    private fun startCalculations() {
        // Сбрасываем значения, если запускаем не в первый раз
        currentNumber = 0
        currentSum = 0
        timer?.cancel() // Убиваем старый таймер, если он был

        btnStart.isEnabled = false // Блокируем кнопку от двойных нажатий
        tvStatus.text = "Вычисление запущено..."

        Log.i(TAG, "--- Таймер запущен ---")

        timer = Timer()
        // Метод scheduleAtFixedRate выполняет задачу с фиксированным интервалом
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                // ВАЖНО: Этот код выполняется в фоновом потоке!

                // Условие выхода (дошли до 100)
                if (currentNumber > 100) {
                    timer?.cancel() // Останавливаем таймер

                    // Обновляем UI в главном потоке
                    runOnUiThread {
                        tvStatus.text = "Готово!\nФинальная сумма: $currentSum"
                        btnStart.isEnabled = true
                    }
                    Log.i(TAG, "--- Вычисление окончено. Финальная сумма: $currentSum ---")
                    return
                }

                // Проверяем, является ли число простым
                if (isPrime(currentNumber)) {
                    currentSum += currentNumber
                }

                // Логируем промежуточные результаты (требование методички)
                Log.i(TAG, "Проверяем число: $currentNumber | Текущая сумма простых чисел: $currentSum")

                // Обновляем UI (переключаемся в Main Thread)
                runOnUiThread {
                    tvStatus.text = "Текущее число: $currentNumber\nТекущая сумма: $currentSum"
                }

                currentNumber++ // Увеличиваем счетчик для следующей секунды
            }
        }, 0, 1000) // 0 - задержка перед стартом, 1000 - интервал в миллисекундах (1 секунда)
    }

    // Вспомогательная функция для проверки простого числа
    private fun isPrime(n: Int): Boolean {
        if (n <= 1) return false
        for (i in 2..Math.sqrt(n.toDouble()).toInt()) {
            if (n % i == 0) return false
        }
        return true
    }

    // Хорошая практика: останавливать фоновые процессы, если приложение свернули/закрыли
    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
}