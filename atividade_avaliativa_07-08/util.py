def limpar_dados(sublista):
    return [nome.strip().upper().replace(" ", "_") for nome in sublista]

def somar_sublista(sublista):
    return sum(sublista)