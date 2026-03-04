# Create image based on the official Node image from dockerhub
FROM node:22-alpine AS build

# Create app directory
WORKDIR /usr/src/compile

# Copy dependency definitions
COPY package*.json /usr/src/compile

# Install dependecies
#RUN npm set progress=false \
#    && npm config set depth 0 \
#    && npm i install
RUN npm ci

# Get all the code needed to run the app
COPY . /usr/src/compile

CMD ["npm", "run", "build"]

FROM node:22-alpine
COPY --from=build /usr/src/compile/dist/app-frontend /usr/src/app
CMD ["npm", "run", "start"]
# Expose the port the app runs in
EXPOSE 80