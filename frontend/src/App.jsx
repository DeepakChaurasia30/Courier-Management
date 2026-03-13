import React from 'react'
import MainLayout from './layouts/MainLayout'
import { Toaster } from "react-hot-toast";

import "./App.css"

const App = () => {
  return (
    <>
          <Toaster position="top-right" reverseOrder={false} />

    <MainLayout />
    </>
  )
}

export default App