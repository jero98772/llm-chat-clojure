; src/llm_chat/core.clj
(ns llm-chat.core
  (:require [clj-http.client :as client]
            [cheshire.core :as json]
            [clojure.string :as str]
            [clojure.tools.cli :refer [parse-opts]])
  (:gen-class))

(def cli-options
  [["-u" "--url URL" "API Base URL"
    :default "http://localhost:1234/v1"]
   ["-k" "--api-key KEY" "API Key"
    :default "lm-studio"]
   ["-m" "--model MODEL" "Model name"
    :default "TheBloke/dolphin-2.2.1-mistral-7B-GGUF"]
   ["-t" "--temperature TEMP" "Temperature"
    :default 1.1
    :parse-fn #(Float/parseFloat %)
    :validate [#(and (>= % 0) (<= % 2)) "Must be between 0 and 2"]]
   ["-l" "--max-tokens TOKENS" "Max tokens"
    :default 140
    :parse-fn #(Integer/parseInt %)
    :validate [#(> % 0) "Must be positive"]]
   ["-h" "--help"]])

(defn chat-completion
  "Make a chat completion request to the OpenAI-compatible API"
  [{:keys [base-url api-key model temperature max-tokens]} messages]
  (try
    (let [response (client/post (str base-url "/chat/completions")
                               {:headers {"Content-Type" "application/json"
                                          "Authorization" (str "Bearer " api-key)}
                                :body (json/generate-string
                                       {:model model
                                        :messages messages
                                        :temperature temperature
                                        :max_tokens max-tokens})
                                :as :json})]
      (-> response
          :body
          :choices
          first
          :message
          :content))
    (catch Exception e
      (println "Error making API request:" (.getMessage e))
      "Sorry, I encountered an error communicating with the LLM server.")))

(defn run-chat
  "Main chat loop"
  [options]
  (println "\nWelcome to the Clojure LLM Chat Interface!")
  (println (str "Connected to: " (:model options) " at " (:base-url options)))
  (println "Type your messages and press Enter. Type 'exit' or 'quit' to end the chat.")
  (println "------------------------------------------------------------")

  (loop [history [{:role "system"
                   :content "You are a helpful assistant."}]]
    (print "\nYou: ")
    (flush)
    (let [user-input (str/trim (read-line))]
      (if (contains? #{"exit" "quit"} (str/lower-case user-input))
        (println "\nGoodbye!")
        (let [updated-history (conj history {:role "user" :content user-input})
              assistant-response (chat-completion options updated-history)
              new-history (conj updated-history {:role "assistant" :content assistant-response})]
          (println "\nAssistant:" assistant-response)
          (recur new-history))))))

(defn -main
  [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    (cond
      (:help options)
      (do (println "Usage: lein run [options]")
          (println summary))
      
      errors
      (do (println "Error parsing command line arguments:")
          (doseq [error errors]
            (println error))
          (println "\nUsage: lein run [options]")
          (println summary))
      
      :else
      (run-chat {:base-url (:url options)
                 :api-key (:api-key options)
                 :model (:model options)
                 :temperature (:temperature options)
                 :max-tokens (:max-tokens options)}))))