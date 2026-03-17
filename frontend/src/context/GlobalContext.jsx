import React, { createContext, useState } from "react";

// Create the context
export const GlobalContext = createContext();

export const GlobalProvider = ({ children }) => {
  // Example global state
  const clientID = 1;

  return (
    <GlobalContext.Provider
      value={{
        clientID,
      }}
    >
      {children}
    </GlobalContext.Provider>
  );
};