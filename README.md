# seeMe
Aplikacja ułatwiająca spotkanie z przyjaciółmi i nie tylko.

Dane przechowywane w aplikacji:

Nie przewiduje potrzeby trzymania duzych danych wiec korzystam z SharedPreferences.

"group":
    "curr_group" - trzyma nazwe aktywnej grupy
    nazwy grup - wartosc dla kazdej nazwy to ".", nieuzywane
"name":
    "name" - trzyma aktywna nazwe uzytkownika
"members"+groupName:
    adresy ipv4 uzytkownikow w grupie
"time"+groupName:
    daty obecnosci + nazwa uzytkonika w formacie: $day/$month $min1 $min2 $user

Komunikacja:

Aplikacja używa dwóch socket'ow.
Pierwszy (#1) odpowiada za wysylanie tresci, a nastepnie czeka na powiadomienie, czy wszystko przebieglo poprawnie.
Drugi (#2) nasłuchuje tresci, odsyla powiadomienie.

Zalozmy, ze mamy dwie aplikacje A oraz B.
A#1 komunikuje sie z B#2, a A#2 z B#1.

#1 wysyla nastepujace tresci, przyjmuje powiadomienia.
#2 przyjmuje tresci, wysyla powiadomienia.

Tresci:
dodanie nowej daty: $day/$month $min1 $min2 $user $group
zaproszenie do grupy: invite $group
dodanie nowego czlonka do grupy: member $group $ipv4

$min1, $min2 - godzina zapisana jako 60*h+m, np 13:30 = 60*13+30 = 810

Powiadomienia:
odbior poprawnej tresci: OK
odbior niepoprawnej tresci WRONG // w przyszlosci nie powinno to byc potrzebne
