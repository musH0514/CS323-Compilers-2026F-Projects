int finalPrice(int unitPrice, int quantity) {
    int amount;
    amount = unitPrice * quantity;
    amount = amount + 9;
    amount = amount - 4;
    return amount;
}

int main() {
    return finalPrice(5, 3);
}