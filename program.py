import requests
from bs4 import BeautifulSoup
import firebase_admin
from firebase_admin import db, credentials
import locale
import json
import re


def parsing(url):
    page = requests.get(url)
    soup = BeautifulSoup(page.text, "html.parser")
    return soup


# van direction
direction = 1


def carrefour_discount():
    url = "https://carrefour.ro"

    carrefour_items = []

    carrefour_soup = parsing(url)

    carrefour_discount = carrefour_soup.find_all("div", {"class": "block-content"})

    for i in carrefour_discount[3].find_all("li"):
        carrefour_discount_array = []
        try:
            carrefour_discount_array.append(i.find("div", {"class": "productItem-name"}).get_text().strip())
        except AttributeError:
            pass
        try:
            priceL = i.find("div", {"class": "price price-old"}).get_text().strip().split('\n')
            price = "{0}.{1}".format(priceL[0].replace(".", ""), priceL[2])
            price = locale.atof(price)
            carrefour_discount_array.append(price)
        except AttributeError:
            pass
        try:
            priceL = i.find("div", {"class": "price price-final"}).get_text().strip().split('\n')
            price = "{0}.{1}".format(priceL[0].replace(".", ""), priceL[2])
            price = locale.atof(price)
            carrefour_discount_array.append(price)
        except AttributeError:
            pass
        try:
            carrefour_discount_array.append(i.find("div", {"class": "productItem-image"}).img["data-src"])
        except AttributeError:
            pass

        if len(carrefour_discount_array) < 4: continue
        carrefour_items.append(carrefour_discount_array)

    return carrefour_items


def carrefour_discounts_fill(discounts):
    for i, item in enumerate(discounts):
        ref = db.reference("carrefour/discount/item{0}/name".format(i))

        amount = re.findall("\d?[,.]?\d+ ?[lgkmKLG]+", item[0])
        if len(amount) == 0:
            amount = 1
        else:
            amount = amount[0]

        name = item[0]
        name = name.replace(str(amount), "")
        try:
            ref.set(name[:name.index(",")])
        except ValueError:
            ref.set(name)

        ref = db.reference("carrefour/discount/item{}/amount".format(i))
        ref.set(amount)
        ref = db.reference("carrefour/discount/item{0}/original".format(i))
        ref.set(item[1])
        ref = db.reference("carrefour/discount/item{0}/percent".format(i))
        ref.set(round(item[2] / item[1] * 100))
        ref = db.reference("carrefour/discount/item{0}/current".format(i))
        ref.set(item[2])
        ref = db.reference("carrefour/discount/item{0}/pic".format(i))
        ref.set(item[3])


def bringo_cheap(url):
    bringo_items = []

    bringo_soup = parsing(url)

    bringo_discount = bringo_soup.find_all("div", {"class": "col-product-listing-box"})

    for i in range(len(bringo_discount)):
        bringo_discount_array = []
        dict = json.loads(bringo_discount[i].form["data-afanalytics"].replace("\n", " "))

        try:
            bringo_discount_array.append(dict["productName"])
        except AttributeError:
            pass
        try:
            bringo_discount_array.append(dict["price"])
        except AttributeError:
            pass
        try:
            bringo_discount_array.append(bringo_discount[i].find("div", {"class": "box-product"}).img["src"])
        except AttributeError:
            pass

        if len(bringo_discount_array) < 3: continue

        bringo_items.append(bringo_discount_array)

    return bringo_items


def carrefour_items_fill(items):
    for i, item in enumerate(items):
        ref = db.reference("carrefour/item{0}/name".format(i))

        amount = re.findall("\d?[,.]?\d+ ?[lgkmKLG]+", item[0])
        if len(amount) == 0:
            amount = 1
        else:
            amount = amount[0]

        name = item[0]
        name = name.replace(str(amount[0]), "")
        try:
            ref.set(name[:name.index(",")])
        except ValueError:
            ref.set(name)

        ref = db.reference("carrefour/item{}/amount".format(i))
        ref.set(amount)
        ref = db.reference("carrefour/item{0}/current".format(i))
        ref.set(item[1])


def carrefour_items_fill_one(items, category):
    ref = db.reference("carrefour/items")
    try:
        i = len(dict(ref.get()))
    except TypeError:
        i = 0

    item = items[0]
    ref = db.reference("carrefour/items/item{}/name".format(i))

    amount = re.findall("\d?[,.]?\d+ ?[lgkmKLG]+", item[0])
    if len(amount) == 0:
        amount = 1
    else:
        amount = amount[0]

    name = item[0]
    name = name.replace(str(amount), "")

    try:
        ref.set(name[:name.index(",")])
    except ValueError:
        ref.set(name)

    ref = db.reference("carrefour/items/item{}/amount".format(i))
    ref.set(amount)
    ref = db.reference("carrefour/items/item{}/current".format(i))
    ref.set(item[1])
    ref = db.reference("carrefour/items/item{}/category".format(i))
    ref.set(category)


def penny_items_fill_one(items, category):
    ref = db.reference("penny/items")
    try:
        i = len(dict(ref.get()))
    except TypeError:
        i = 0

    item = items[0]

    amount = re.findall("\d?[,.]?\d+ ?[lgkmKLG]+", item[0])
    if len(amount) == 0:
        amount = 1
    else:
        amount = amount[0]

    ref = db.reference("penny/items/item{}/name".format(i))
    name = item[0]
    name = name.replace(str(amount), "")

    try:
        ref.set(name[:name.index(",")])
    except ValueError:
        ref.set(name)
    ref = db.reference("penny/items/item{}/amount".format(i))
    ref.set(amount)
    ref = db.reference("penny/items/item{}/current".format(i))
    ref.set(item[1])
    ref = db.reference("penny/items/item{}/category".format(i))
    ref.set(category)
    ref = db.reference("penny/items/item{}/pic".format(i))
    ref.set(item[2])


def auchan_discounts():
    auchan_items = []

    url = "https://www.auchan.ro/promotii?page=2"

    auchan_title = parsing(url).find_all("span", {
        "class": "vtex-product-summary-2-x-productBrand vtex-product-summary-2-x-productBrand--defaultShelf vtex-product-summary-2-x-brandName vtex-product-summary-2-x-brandName--defaultShelf t-body"})

    for elem in auchan_title:
        print(elem.get_text())

    return auchan_items


def kaufland_discount():
    kaufland_items = []
    url = "https://www.kaufland.ro/oferte/oferte-saptamanale/saptamana-curenta.category=01_Carne__mezeluri.html"
    soup = parsing(url)

    kaufland_discount = soup.find_all("div", {
        "class": "m-offer-tile m-offer-tile--line-through m-offer-tile--uppercase-subtitle m-offer-tile--mobile"})
    for i in range(len(kaufland_discount)):
        kaufland_discount_array = []
        try:
            kaufland_discount_array.append(
                kaufland_discount[i].find("h5", {"class": "m-offer-tile__subtitle"}).get_text().strip())
        except AttributeError:
            kaufland_discount_array += ""
        try:
            kaufland_discount_array.append(
                kaufland_discount[i].find("h4", {"class": "m-offer-tile__title"}).get_text().strip())
        except AttributeError:
            kaufland_discount_array += ""
        try:
            kaufland_discount_array.append(
                kaufland_discount[i].find("div", {"class": "m-offer-tile__quantity"}).get_text().strip())
        except AttributeError:
            kaufland_discount_array += ""
        try:
            kaufland_discount_array.append(
                kaufland_discount[i].img["data-src"])
        except AttributeError:
            kaufland_discount_array += ""
        try:
            kaufland_discount_array.append(
                kaufland_discount[i].find("div", {"class": "a-pricetag__old-price"}).get_text().strip())
        except AttributeError:
            kaufland_discount_array += ""
        try:
            kaufland_discount_array.append(
                kaufland_discount[i].find("div", {"class": "a-pricetag__discount"}).get_text().strip())
        except AttributeError:
            kaufland_discount_array += ""
        try:
            kaufland_discount_array.append(
                kaufland_discount[i].find("div", {"class": "a-pricetag__price"}).get_text().strip())
        except AttributeError:
            kaufland_discount_array += ""

        kaufland_items.append(kaufland_discount_array)

    return kaufland_items


def kaufland_discounts_fill(discounts, ):
    for i, item in enumerate(discounts):
        if len(item) < 7:
            continue
        ref = db.reference("kaufland/discount/item{0}/name".format(i))
        name = "{} {}".format(item[0], item[1])
        try:
            ref.set(name[:name.index(",")])
        except ValueError:
            ref.set(name)
        ref = db.reference("kaufland/discount/item{0}/amount".format(i))
        ref.set(item[2])
        ref = db.reference("kaufland/discount/item{0}/pic".format(i))
        ref.set(item[3])
        ref = db.reference("kaufland/discount/item{0}/original".format(i))
        ref.set(item[4])
        ref = db.reference("kaufland/discount/item{0}/percent".format(i))
        ref.set(item[5])
        ref = db.reference("kaufland/discount/item{0}/current".format(i))
        ref.set(item[6])


def main():
    # Fetch the service account key JSON file contents
    cred = credentials.Certificate('key.json')

    # Initialize the app with a service account, granting admin privileges
    firebase_admin.initialize_app(cred, {
        'databaseURL': "https://shopsmart-e3179-default-rtdb.europe-west1.firebasedatabase.app"
    })

    # kaufland_discounts = kaufland_discount()
    # kaufland_discounts_fill(kaufland_discounts)

    # carrefour_discounts = carrefour_discount()
    # carrefour_discounts_fill(carrefour_discounts)

    # penny_cheap_ = bringo_cheap(
    #     "https://www.bringo.ro/ro/store/penny-penny-targu-mures-portico/unt-si-margarina?sorting%5Bprice%5D=asc")
    # penny_items_fill_one(penny_cheap_, "unt")

    # carrefour_cheap_ = bringo_cheap(
    #     "")
    # carrefour_items_fill_one(carrefour_cheap_, "bere")


if __name__ == '__main__':
    main()
