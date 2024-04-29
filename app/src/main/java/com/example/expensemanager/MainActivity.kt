package com.example.expensemanager

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TransactionAdapter
    private val transactionsList = mutableListOf<Transaction>()
    private var totalExpenses: Double = 0.0
    private var totalIncome: Double = 0.0
    private var balance: Double = 0.0
    private var Max_e=10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fabAddTransaction: FloatingActionButton = findViewById(R.id.fabAddTransaction)
        val llTransactionOptions: LinearLayout = findViewById(R.id.llTransactionOptions)
        val btnAddExpense: Button = findViewById(R.id.btnAddExpense)
        val btnAddIncome: Button = findViewById(R.id.btnAddIncome)

        fabAddTransaction.setOnClickListener {
            toggleTransactionOptions(llTransactionOptions)
        }

        btnAddExpense.setOnClickListener {
            showAddTransactionDialog("Expense")
        }

        btnAddIncome.setOnClickListener {
            showAddTransactionDialog("Income")
        }

        recyclerView = findViewById(R.id.recyclerViewTransactions)
        adapter = TransactionAdapter(transactionsList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
    }

    private fun toggleTransactionOptions(llTransactionOptions: LinearLayout) {
        if (llTransactionOptions.visibility == View.GONE) {
            llTransactionOptions.visibility = View.VISIBLE
        } else {
            llTransactionOptions.visibility = View.GONE
        }
    }

    private fun showAddTransactionDialog(type: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add $type")

        val inflater = LayoutInflater.from(this)
        val view = inflater.inflate(R.layout.dialog_add_transaction, null)
        builder.setView(view)

        val editTextTitle = view.findViewById<EditText>(R.id.editTextTitle)
        val editTextDescription = view.findViewById<EditText>(R.id.editTextDescription)
        val editTextAmount = view.findViewById<EditText>(R.id.editTextAmount)
        val editTextDate = view.findViewById<EditText>(R.id.editTextDate)

        editTextDate.setOnClickListener {
            showDatePickerDialog(editTextDate)
        }

        builder.setPositiveButton("Add") { dialog, which ->
            val title = editTextTitle.text.toString()
            val description = editTextDescription.text.toString()
            val amount = editTextAmount.text.toString().toDoubleOrNull()
            val date = editTextDate.text.toString()

            if (title.isNotEmpty() && amount != null && date.isNotEmpty()) {
                val newTransaction = Transaction(title, amount, date)
                addTransaction(newTransaction, type == "Expense")
                dialog.dismiss()
            } else {
                Toast.makeText(this, "Please fill all fields correctly", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun addTransaction(transaction: Transaction, isExpense: Boolean) {
        val signedAmount = if (isExpense) -transaction.amount else transaction.amount
        transactionsList.add(transaction.copy(amount = signedAmount))

        if (isExpense) {
            totalExpenses += transaction.amount
        } else {
            totalIncome += transaction.amount
        }
        balance = totalIncome - totalExpenses

        updateUI()

        adapter.notifyDataSetChanged()

    }

    private fun updateUI() {
        val textViewBalance: TextView = findViewById(R.id.textViewBalance)
        val textViewExpensesAmount: TextView = findViewById(R.id.textViewExpensesAmount)
        val textViewIncomeAmount: TextView = findViewById(R.id.textViewIncomeAmount)

        textViewBalance.text = "Balance: ₹ $balance"
        textViewExpensesAmount.text = "₹ $totalExpenses"
        textViewIncomeAmount.text = "₹ $totalIncome"
    }

    private fun showDatePickerDialog(editTextDate: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                val selectedDate = "${dayOfMonth}/${month + 1}/${year}"
                editTextDate.setText(selectedDate)
            }, year, month, dayOfMonth
        )
        datePickerDialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}