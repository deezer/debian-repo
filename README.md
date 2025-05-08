# Deezer Debian repository

## How do I add this repository?

Run the following commands in a terminal
```
mkdir -p /usr/local/share/keyrings
sudo curl -sfLo /usr/local/share/keyrings/deezer.gpg https://research.deezer.com/debian-repo/gpg.key
echo "deb [signed-by=/usr/share/local/keyrings/deezer.gpg] https://research.deezer.com/debian-repo/ stable main | sudo tee -a /etc/apt/sources.list.d/deezer.list
```
