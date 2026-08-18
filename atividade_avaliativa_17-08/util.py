def calcular_faturamento_filial(vendas_filial, id_filial):
    soma_local = sum(vendas_filial)
    print(f"filial {id_filial} processamento concluido. subtotal: r$ {soma_local:.2f}")
    
    return soma_local