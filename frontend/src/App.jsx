import React from 'react'
import MainLayout from './layouts/MainLayout'
import { Toaster } from "react-hot-toast";
import { GlobalProvider } from './context/GlobalContext';

import "./App.css"

const App = () => {
  return (
    <>
        <Toaster position="top-right" reverseOrder={false} />
      <GlobalProvider>
        <MainLayout />
      </GlobalProvider>
    
    </>
  )
}

export default App