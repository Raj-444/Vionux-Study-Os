// lib/finance_screen.dart

import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:fl_chart/fl_chart.dart';
import 'finance_provider.dart';
import 'transaction_model.dart';

class FinanceScreen extends StatefulWidget {
  const FinanceScreen({Key? key}) : super(key: key);

  @override
  State<FinanceScreen> createState() => _FinanceScreenState();
}

class _FinanceScreenState extends State<FinanceScreen> {
  // Color palette matching the Focus Space design system
  final Map<String, Color> _categoryColors = {
    "Food": const Color(0xFFE94560),          // Accent Red
    "Transportation": const Color(0xFF0F3460),  // Deep Slate Blue
    "Study Materials": const Color(0xFFE2B024), // Vibrant Gold
    "Entertainment": const Color(0xFF22C55E),   // Rich Emerald
    "Other": const Color(0xFF8E8E93),           // Cool Grey
  };

  @override
  void initState() {
    super.initState();
    // Load fresh transactions from the DB on load
    WidgetsBinding.instance.addPostFrameCallback((_) {
      Provider.of<FinanceProvider>(context, listen: false).loadTransactions();
    });
  }

  void _showAddTransactionDialog() {
    final TextEditingController titleController = TextEditingController();
    final TextEditingController amountController = TextEditingController();
    String selectedCategory = "Food";
    String selectedType = "expense";

    showDialog(
      context: context,
      builder: (context) {
        return StatefulBuilder(
          builder: (context, setDialogState) {
            return AlertDialog(
              backgroundColor: const Color(0xFFF8F9FA),
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(24)),
              title: const Text(
                'New Transaction',
                style: TextStyle(fontWeight: FontWeight.bold),
              ),
              content: SingleChildScrollView(
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    // Segmented Selector for Income / Expense
                    Row(
                      children: [
                        Expanded(
                          child: ChoiceChip(
                            label: const Center(child: Text("Expense")),
                            selected: selectedType == "expense",
                            selectedColor: const Color(0xFFE94560).withOpacity(0.15),
                            labelStyle: TextStyle(
                              color: selectedType == "expense" ? const Color(0xFFE94560) : Colors.grey,
                              fontWeight: FontWeight.bold,
                            ),
                            onSelected: (val) {
                              if (val) setDialogState(() => selectedType = "expense");
                            },
                          ),
                        ),
                        const SizedBox(width: 12),
                        Expanded(
                          child: ChoiceChip(
                            label: const Center(child: Text("Income")),
                            selected: selectedType == "income",
                            selectedColor: const Color(0xFF22C55E).withOpacity(0.15),
                            labelStyle: TextStyle(
                              color: selectedType == "income" ? const Color(0xFF22C55E) : Colors.grey,
                              fontWeight: FontWeight.bold,
                            ),
                            onSelected: (val) {
                              if (val) setDialogState(() => selectedType = "income");
                            },
                          ),
                        ),
                      ],
                    ),
                    const SizedBox(height: 16),
                    TextField(
                      controller: titleController,
                      decoration: InputDecoration(
                        labelText: 'Transaction Title',
                        hintText: 'e.g., Textbooks, Lunch',
                        filled: true,
                        fillColor: Colors.white,
                        border: OutlineInputBorder(
                          borderRadius: BorderRadius.circular(16),
                          borderSide: BorderSide.none,
                        ),
                      ),
                    ),
                    const SizedBox(height: 12),
                    TextField(
                      controller: amountController,
                      keyboardType: const TextInputType.numberWithOptions(decimal: true),
                      decoration: InputDecoration(
                        labelText: 'Amount (\$)',
                        hintText: 'e.g., 24.50',
                        filled: true,
                        fillColor: Colors.white,
                        border: OutlineInputBorder(
                          borderRadius: BorderRadius.circular(16),
                          borderSide: BorderSide.none,
                        ),
                      ),
                    ),
                    const SizedBox(height: 12),
                    DropdownButtonFormField<String>(
                      value: selectedCategory,
                      decoration: InputDecoration(
                        labelText: 'Category',
                        filled: true,
                        fillColor: Colors.white,
                        border: OutlineInputBorder(
                          borderRadius: BorderRadius.circular(16),
                          borderSide: BorderSide.none,
                        ),
                      ),
                      items: _categoryColors.keys.map((cat) {
                        return DropdownMenuItem(
                          value: cat,
                          child: Text(cat),
                        );
                      }).toList(),
                      onChanged: (val) {
                        if (val != null) {
                          setDialogState(() {
                            selectedCategory = val;
                          });
                        }
                      },
                    ),
                  ],
                ),
              ),
              actions: [
                TextButton(
                  onPressed: () => Navigator.pop(context),
                  child: const Text('Cancel', style: TextStyle(color: Colors.grey)),
                ),
                ElevatedButton(
                  style: ElevatedButton.styleFrom(
                    backgroundColor: const Color(0xFFE94560),
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(14),
                    ),
                  ),
                  onPressed: () {
                    final title = titleController.text.trim();
                    final amount = double.tryParse(amountController.text) ?? 0.0;
                    if (title.isEmpty || amount <= 0) return;

                    final newTx = TransactionModel(
                      title: title,
                      amount: amount,
                      date: DateTime.now(),
                      category: selectedCategory,
                      type: selectedType,
                    );

                    Provider.of<FinanceProvider>(context, listen: false).addTransaction(newTx);
                    Navigator.pop(context);
                  },
                  child: const Text('Add', style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold)),
                ),
              ],
            );
          },
        );
      },
    );
  }

  List<PieChartSectionData> _buildPieSections(List<TransactionModel> transactions) {
    // 1. Filter expense transactions
    final expenses = transactions.where((t) => t.type == 'expense').toList();
    if (expenses.isEmpty) {
      // Return a placeholder section if there are no expenses yet
      return [
        PieChartSectionData(
          color: Colors.grey.shade300,
          value: 100,
          title: "No Expenses",
          radius: 40,
          titleStyle: const TextStyle(fontSize: 12, fontWeight: FontWeight.bold, color: Colors.grey),
        )
      ];
    }

    // 2. Sum expenses per category
    final Map<String, double> categorySums = {};
    double totalExpenseSum = 0.0;

    for (var tx in expenses) {
      categorySums[tx.category] = (categorySums[tx.category] ?? 0.0) + tx.amount;
      totalExpenseSum += tx.amount;
    }

    // 3. Convert sums to fl_chart slice sections
    return categorySums.entries.map((entry) {
      final String category = entry.key;
      final double amount = entry.value;
      final double percentage = (amount / totalExpenseSum) * 100;
      final Color color = _categoryColors[category] ?? Colors.grey;

      return PieChartSectionData(
        color: color,
        value: amount,
        title: '${percentage.toStringAsFixed(0)}%',
        radius: 40,
        titleStyle: const TextStyle(
          fontSize: 12,
          fontWeight: FontWeight.bold,
          color: Colors.white,
        ),
      );
    }).toList();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF8F9FA),
      appBar: AppBar(
        backgroundColor: Colors.white,
        elevation: 0,
        title: const Text(
          "Personal Finance",
          style: TextStyle(fontWeight: FontWeight.bold, color: Color(0xFF0C0C0E)),
        ),
        centerTitle: true,
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: _showAddTransactionDialog,
        backgroundColor: const Color(0xFFE94560),
        shape: const CircleBorder(),
        child: const Icon(Icons.add, color: Colors.white),
      ),
      body: Consumer<FinanceProvider>(
        builder: (context, provider, child) {
          final transactions = provider.transactions;
          final totalIncome = transactions
              .where((t) => t.type == 'income')
              .fold<double>(0.0, (sum, item) => sum + item.amount);
          final totalExpense = transactions
              .where((t) => t.type == 'expense')
              .fold<double>(0.0, (sum, item) => sum + item.amount);
          final netBalance = totalIncome - totalExpense;

          return Column(
            children: [
              // Premium Balance Cards
              Container(
                color: Colors.white,
                padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 16),
                child: Column(
                  children: [
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        _buildStatBox("Total Income", "\$${totalIncome.toStringAsFixed(2)}", const Color(0xFF22C55E)),
                        _buildStatBox("Total Expense", "\$${totalExpense.toStringAsFixed(2)}", const Color(0xFFE94560)),
                      ],
                    ),
                    const SizedBox(height: 16),
                    Container(
                      width: double.infinity,
                      padding: const EdgeInsets.symmetric(vertical: 12),
                      decoration: BoxDecoration(
                        color: const Color(0xFF0C0C0E),
                        borderRadius: BorderRadius.circular(16),
                      ),
                      child: Center(
                        child: Text(
                          "Net Balance: \$${netBalance.toStringAsFixed(2)}",
                          style: TextStyle(
                            color: netBalance >= 0 ? const Color(0xFF22C55E) : const Color(0xFFE94560),
                            fontWeight: FontWeight.bold,
                            fontSize: 16,
                          ),
                        ),
                      ),
                    ),
                  ],
                ),
              ),

              // Pie Chart Visualization Container
              Container(
                margin: const EdgeInsets.all(20),
                height: 180,
                decoration: BoxDecoration(
                  color: Colors.white,
                  borderRadius: BorderRadius.circular(24),
                  boxShadow: [
                    BoxShadow(
                      color: Colors.black.withOpacity(0.02),
                      blurRadius: 16,
                      offset: const Offset(0, 8),
                    )
                  ],
                ),
                child: Row(
                  children: [
                    const SizedBox(width: 24),
                    // Real Data Pie Chart Widget
                    Expanded(
                      flex: 4,
                      child: PieChart(
                        PieChartData(
                          sectionsSpace: 2,
                          centerSpaceRadius: 36,
                          sections: _buildPieSections(transactions),
                        ),
                      ),
                    ),
                    const SizedBox(width: 16),
                    // Legend Column
                    Expanded(
                      flex: 5,
                      child: SingleChildScrollView(
                        child: Column(
                          mainAxisAlignment: MainAxisAlignment.center,
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: _categoryColors.entries.map((entry) {
                            final category = entry.key;
                            final color = entry.value;
                            final categoryAmount = transactions
                                .where((t) => t.type == 'expense' && t.category == category)
                                .fold<double>(0.0, (sum, item) => sum + item.amount);

                            return Padding(
                              padding: const EdgeInsets.symmetric(vertical: 3.0),
                              child: Row(
                                children: [
                                  Container(
                                    width: 10,
                                    height: 10,
                                    decoration: BoxDecoration(
                                      color: color,
                                      shape: BoxShape.circle,
                                    ),
                                  ),
                                  const SizedBox(width: 8),
                                  Expanded(
                                    child: Text(
                                      "$category (\$${categoryAmount.toStringAsFixed(0)})",
                                      style: const TextStyle(
                                        fontSize: 12,
                                        fontWeight: FontWeight.bold,
                                        color: Color(0xFF0C0C0E),
                                      ),
                                      overflow: TextOverflow.ellipsis,
                                    ),
                                  ),
                                ],
                              ),
                            );
                          }).toList(),
                        ),
                      ),
                    ),
                  ],
                ),
              ),

              // Scrollable Transaction List
              Expanded(
                child: Container(
                  width: double.infinity,
                  decoration: const BoxDecoration(
                    color: Colors.white,
                    borderRadius: BorderRadius.only(
                      topLeft: Radius.circular(32),
                      topRight: Radius.circular(32),
                    ),
                  ),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      const Padding(
                        padding: EdgeInsets.only(left: 24, top: 24, bottom: 12),
                        child: Text(
                          "Transactions History",
                          style: TextStyle(
                            fontSize: 16,
                            fontWeight: FontWeight.bold,
                            color: Color(0xFF0C0C0E),
                          ),
                        ),
                      ),
                      Expanded(
                        child: transactions.isEmpty
                            ? const Center(
                                child: Text(
                                  "No transactions logged yet.",
                                  style: TextStyle(color: Colors.grey),
                                ),
                              )
                            : ListView.builder(
                                padding: const EdgeInsets.symmetric(horizontal: 20),
                                itemCount: transactions.length,
                                itemBuilder: (context, index) {
                                  final tx = transactions[index];
                                  final isExpense = tx.type == "expense";
                                  final Color categoryColor = _categoryColors[tx.category] ?? Colors.grey;

                                  return Card(
                                    color: const Color(0xFFF8F9FA),
                                    elevation: 0,
                                    shape: RoundedRectangleBorder(
                                      borderRadius: BorderRadius.circular(16),
                                    ),
                                    margin: const EdgeInsets.symmetric(vertical: 6),
                                    child: ListTile(
                                      leading: Container(
                                        width: 12,
                                        height: 12,
                                        decoration: BoxDecoration(
                                          color: categoryColor,
                                          shape: BoxShape.circle,
                                        ),
                                      ),
                                      title: Text(
                                        tx.title,
                                        style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 14),
                                      ),
                                      subtitle: Text(
                                        "${tx.category} • ${tx.date.toString().substring(0, 10)}",
                                        style: const TextStyle(fontSize: 11, color: Colors.grey),
                                      ),
                                      trailing: Row(
                                        mainAxisSize: MainAxisSize.min,
                                        children: [
                                          Text(
                                            "${isExpense ? '-' : '+'}\$${tx.amount.toStringAsFixed(2)}",
                                            style: TextStyle(
                                              color: isExpense ? const Color(0xFFE94560) : const Color(0xFF22C55E),
                                              fontWeight: FontWeight.bold,
                                              fontSize: 14,
                                            ),
                                          ),
                                          const SizedBox(width: 8),
                                          if (tx.id != null)
                                            IconButton(
                                              icon: const Icon(Icons.delete_outline_rounded, color: Colors.grey, size: 20),
                                              onPressed: () {
                                                provider.deleteTransaction(tx.id!);
                                              },
                                            ),
                                        ],
                                      ),
                                    ),
                                  );
                                },
                              ),
                      ),
                    ],
                  ),
                ),
              ),
            ],
          );
        },
      ),
    );
  }

  Widget _buildStatBox(String label, String amount, Color color) {
    return Container(
      width: (MediaQuery.of(context).size.width - 60) / 2,
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: const Color(0xFFF8F9FA),
        borderRadius: BorderRadius.circular(20),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            label,
            style: const TextStyle(color: Colors.grey, fontSize: 12, fontWeight: FontWeight.bold),
          ),
          const SizedBox(height: 4),
          Text(
            amount,
            style: TextStyle(color: color, fontSize: 18, fontWeight: FontWeight.bold),
          ),
        ],
      ),
    );
  }
}
