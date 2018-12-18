def convert_to_bin(hex_string,cider):

    hex_value = ( str(f'{int(hex_string, 16):0>32b}'))
    bin_value = hex_value[0:int(cider)]
    return bin_value

def make_into_network(full_network):

    if "default" in full_network:
        default = ["default", full_network[len(full_network)-1] ]
        return default
    #['ff.ff.ff.ff', '20 A']
    address_cider_hop = full_network.split("/")
    address_hex = address_cider_hop[0].replace(".","")
    cider_hop = address_cider_hop[1].split(" ")
    cider = cider_hop[0]
    hop = cider_hop[1]
    address = convert_to_bin(address_hex,cider)
    network = [address, hop]
    return network

def find_longest_matching_network(ip_addresses,networks):

    next_hop_list = []
    for x in ip_addresses:
        longest_match = None
        for y in networks:
            if y[0] in x[0]:
                ##Network = y,  Address = x
                if longest_match == None:
                    longest_match = [x,y,len(y[0])]
                else:
                    if len(y[0]) > longest_match[2]:
                        longest_match = [x,y,len(y[0])]
        if longest_match == None:
            longest_match = [x,default_hop,0]
        next_hop_list.append([longest_match[0][1],longest_match[1][1]])
    print("Hop List    :    ", next_hop_list)
    return next_hop_list

def make_table_of_networks():

    network_table = []
    for x in routing_table:
        network_table.append(make_into_network(x))
    return network_table

def make_table_of_addresses():

    addess_table = []
    for x in ip_addresses:
        addess_table.append([address_to_bin(x),x])
    return addess_table

def address_to_bin(address):

    address_hex = address.replace(".","")
    address_bin = convert_to_bin(address_hex, 32)
    return address_bin

if __name__ == '__main__':

    default_hop = None
    ip_addresses = open("IPAddresses.txt").read().split("\n")
    routing_table = open("RoutingTable.txt").read().split("\n")

    networks = make_table_of_networks()
    default_hop = networks.pop()
    addresses = make_table_of_addresses()

    find_longest_matching_network(addresses,networks)