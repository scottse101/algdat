import sys
import time
import random
def find_largest_difference(data):
    lowest_pointer = 0
    buy_price = sys.maxsize
    highest_pointer = 0
    sell_price = 0
    best_deal = {"purchase_day" : 0, "sell_day" : 0, "difference" : 0}
    current_shareprice = 0
    for i in range(0, len(data)):
        current_shareprice = current_shareprice + data[i]
    if current_shareprice < buy_price:
        buy_price = current_shareprice
    lowest_pointer = i
    sell_price = current_shareprice
    highest_pointer = i
    elif current_shareprice > sell_price:
    sell_price = current_shareprice
    highest_pointer = i
    if sell_price - buy_price > best_deal["difference"]:
        best_deal["purchase_day"] = lowest_pointer + 1
    best_deal["sell_day"] = highest_pointer + 1
    best_deal["difference"] = sell_price - buy_price
    return best_deal
def initialize_array(n):
    array = []
    for i in range(0, n):
        array.append(random.randint(-10, 10))
    return array
runder = 0
test_data = initialize_array(10000000)
start = time.time()
slutt = time.time()
while True:
    best_deal = find_largest_difference(test_data)
    slutt = time.time()
    runder = runder + 1
    if slutt - start > 1:
        break
print(((slutt - start)/runder)*1000, " milliseconds per loop", best_deal)
