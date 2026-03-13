const Spinner = () => {
  return (
    <div className="absolute inset-0 flex justify-center items-center bg-transparent bg-opacity-50 z-10">
      <div className="w-12 h-12 border-4 border-blue-500 border-t-transparent rounded-full animate-spin"></div>
    </div>
  );
};

export default Spinner;