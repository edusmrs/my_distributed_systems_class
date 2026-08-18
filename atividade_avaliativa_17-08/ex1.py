import threading

# com compartilhamento de memória
saldo_central = 0

lock = threading.Lock()

def vender_fichas(id_caixa, quantidade_fichas, valor_ficha):
    global saldo_central
    
    for _ in range(quantidade_fichas):
        with lock:
            saldo_central += valor_ficha

def main():
    threads = []
    num_caixas = 5
    fichas_por_caixa = 1000
    valor_ficha = 10

    for i in range(num_caixas):
        t = threading.Thread(target=vender_fichas, args=(i+1, fichas_por_caixa, valor_ficha))
        threads.append(t)
        t.start()

    for t in threads:
        t.join()

    valor_esperado = num_caixas * fichas_por_caixa * valor_ficha
    
    print(f"saldo final esperado: r$ {valor_esperado:.2f}")
    print(f"saldo final obtido: r$ {saldo_central:.2f}")

    if saldo_central == valor_esperado:
        print("sucesso")
    else:
        print("falha")

if __name__ == "__main__":
    main()